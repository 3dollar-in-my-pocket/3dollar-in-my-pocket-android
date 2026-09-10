package com.zion830.threedollars.ui.home.design

import android.app.Activity
import android.app.Instrumentation
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDCursorModel
import com.threedollar.common.serverdriven.model.SDLocationBoundsModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.domain.screen.repository.ScreenRepository
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.edit.ui.EditStoreFragment
import com.zion830.threedollars.ui.home.ui.HomeFragment
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.map.ui.NaverMapFragment
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2Activity
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2PlatformAction
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2UiState
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2ViewModel
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlinx.coroutines.flow.flowOf

/** Real Android hosts, with repository replacement confined to this debug test runner. */
class SduiHostInstrumentation : Instrumentation() {
    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        start()
    }

    override fun onStart() {
        val result = runCatching { verifyHomeEditAndClipboard() }
        val output = result.fold(
            onSuccess = { "PASS: Home duplicate Edit/cancel/fake saved result; both host account clipboards; focus viewport and pagination camera; missing-coordinate visit stays open; fake review save refreshes once; V2 Edit back\n" },
            onFailure = { "FAIL: ${it.stackTraceToString()}\n" },
        )
        finish(if (result.isSuccess) Activity.RESULT_OK else Activity.RESULT_CANCELED, Bundle().apply {
            putString("stream", output)
            putBoolean("passed", result.isSuccess)
        })
    }

    private fun verifyHomeEditAndClipboard() {
        val activity = startActivitySync(Intent(targetContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as MainActivity
        await("HomeFragment ready") { findHome(activity.supportFragmentManager)?.isResumed == true }
        val home = main { requireNotNull(findHome(activity.supportFragmentManager)) }
        val homeVm = main { ViewModelProvider(activity)[HomeViewModel::class.java] }
        await("initial Home response") { homeVm.homeListSection.value.cards.isNotEmpty() }
        main {
            // A fresh emulator can restore a camera in the US; use the audited dev store's region.
            val storeRegion = LatLng(37.4815726, 126.8824853)
            homeVm.fetchAroundStores(storeRegion, storeRegion, requestFocusBounds = false)
        }
        await("first Home cards") { homeVm.homeListSection.value.cards.any { it is HomeListCardModel.BasicCard } }
        main {
            homeVm.selectHomeListCard(homeVm.homeListSection.value.cards.filterIsInstance<HomeListCardModel.BasicCard>().first())
        }
        val detailVm = main { ViewModelProvider(home)[StoreDetailV2ViewModel::class.java] }
        await("selected detail") { detailVm.uiState.value is StoreDetailV2UiState.Content }
        main {
            val fragment = requireNotNull(findMap(home.childFragmentManager))
            val map = requireNotNull(fragment.naverMap)
            fragment.onMapReady(map)
            val expected = if (isLocationAvailable()) LocationTrackingMode.NoFollow else LocationTrackingMode.None
            check(map.locationTrackingMode == expected) {
                "Home map-ready lets SDK move the camera: expected $expected, actual ${map.locationTrackingMode}"
            }
        }
        val selectedId = main { requireNotNull(homeVm.selectedStorePreviewStoreId.value) }
        val listSnapshot = main { homeVm.homeListSection.value }
        val detailSnapshot = main { (detailVm.uiState.value as StoreDetailV2UiState.Content).screen }
        val updatedDetail = detailSnapshot.copy(sections = detailSnapshot.sections.map { section ->
            if (section is StoreDetailSectionModel.Preview) {
                section.copy(header = section.header.copy(title = SDTextModel("FAKE UPDATED", false)))
            } else section
        })
        val listRequests = AtomicInteger()
        val detailRequests = AtomicInteger()
        val fake = Proxy.newProxyInstance(ScreenRepository::class.java.classLoader, arrayOf(ScreenRepository::class.java)) { _, method, _ ->
            when (method.name) {
                "getHomeListSection" -> {
                    listRequests.incrementAndGet()
                    flowOf(BaseResponse(ok = true, data = listSnapshot))
                }
                "getStoreDetailScreen" -> {
                    detailRequests.incrementAndGet()
                    flowOf(BaseResponse(ok = true, data = updatedDetail))
                }
                else -> error("Unexpected repository operation in host test: ${method.name}")
            }
        } as ScreenRepository
        val originalHomeRepository = main { replaceRepository(homeVm, fake) }
        val originalDetailRepository = main { replaceRepository(detailVm, fake) }
        val manager = activity.supportFragmentManager
        val editTag = EditStoreFragment::class.java.name
        val open = HomeFragment::class.java.getDeclaredMethod("openStoreEdit", SDCustomActionModel::class.java).apply { isAccessible = true }
        try {
            main {
                val before = manager.backStackEntryCount
                // Dispatch in the same main-loop turn: the first transaction has not committed yet.
                repeat(2) { open.invoke(home, SDCustomActionModel("STORE_EDIT_SECTION_UPDATE", emptyMap())) }
                manager.executePendingTransactions()
                check(manager.backStackEntryCount == before + 1) { "Duplicate Edit back stack entries" }
                check(manager.fragments.count { it is EditStoreFragment } == 1) { "Duplicate Edit fragments" }
                check(home.parentFragmentManager.findFragmentByTag(editTag) == null) { "Edit was added to NavHost manager" }
                manager.popBackStackImmediate()
            }
            waitForIdleSync()
            check(listRequests.get() == 0 && detailRequests.get() == 0) { "Cancel refreshed the store" }
            main {
                open.invoke(home, SDCustomActionModel("STORE_EDIT_SECTION_UPDATE", emptyMap()))
                manager.executePendingTransactions()
                manager.setFragmentResult(EditStoreFragment.STORE_EDITED_RESULT_KEY, Bundle().apply {
                    putBoolean(EditStoreFragment.STORE_UPDATED, true)
                })
                manager.popBackStackImmediate()
            }
            await("fake save refresh") { listRequests.get() >= 1 && detailRequests.get() >= 1 }
            waitForIdleSync()
            main {
                check(listRequests.get() == 1 && detailRequests.get() == 1) { "Save must refresh list/detail exactly once" }
                check(homeVm.selectedStorePreviewStoreId.value == selectedId) { "Save lost selected store" }
                val title = (detailVm.uiState.value as StoreDetailV2UiState.Content).screen.sections
                    .filterIsInstance<StoreDetailSectionModel.Preview>().first().header.title?.text
                check(title == "FAKE UPDATED") { "Fake saved response was not displayed" }
                assertAccountClipboard(home, "handleStoreDetailV2PlatformAction", activity)
            }
        } finally {
            main {
                replaceRepository(homeVm, originalHomeRepository)
                replaceRepository(detailVm, originalDetailRepository)
            }
        }
        verifyFocusPagination(home, homeVm, listSnapshot)
        verifyMissingLocationVisit(home, homeVm, detailVm, listSnapshot, detailSnapshot, selectedId)
        val full = startActivitySync(StoreDetailV2Activity.getIntent(targetContext, selectedId).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) as StoreDetailV2Activity
        await("V2 detail ready") { ViewModelProvider(full)[StoreDetailV2ViewModel::class.java].uiState.value is StoreDetailV2UiState.Content }
        main {
            assertAccountClipboard(full, "handlePlatformAction", full)
            StoreDetailV2Activity::class.java.getDeclaredMethod("openEditStore", SDCustomActionModel::class.java).apply {
                isAccessible = true
            }.invoke(full, SDCustomActionModel("STORE_EDIT_SECTION_UPDATE", emptyMap()))
            full.supportFragmentManager.executePendingTransactions()
            check(full.supportFragmentManager.fragments.any { it is EditStoreFragment })
        }
        waitForIdleSync()
        main { full.onBackPressedDispatcher.onBackPressed() }
        await("V2 Edit back") { full.supportFragmentManager.fragments.none { it is EditStoreFragment } }
        main { check(!full.isFinishing) { "Edit back closed V2 detail" } }
    }

    private fun verifyMissingLocationVisit(
        home: HomeFragment,
        homeVm: HomeViewModel,
        detailVm: StoreDetailV2ViewModel,
        snapshot: HomeListSectionModel,
        detail: StoreDetailScreenModel,
        storeId: Long,
    ) {
        val markerless = snapshot.cards.filterIsInstance<HomeListCardModel.BasicCard>().first()
            .copy(cardId = "qa-no-coordinates", marker = null, clickLog = null, impressionLog = null)
        val noMap = detail.copy(sections = detail.sections.filterNot { it is StoreDetailSectionModel.Map }.map { section ->
            when (section) {
                is StoreDetailSectionModel.Edit -> section.copy(map = null)
                is StoreDetailSectionModel.Preview -> section.copy(header = section.header.copy(title = SDTextModel("NO COORDINATES", false)))
                else -> section
            }
        })
        val missingListLoads = AtomicInteger()
        val missingDetailLoads = AtomicInteger()
        val fake = Proxy.newProxyInstance(ScreenRepository::class.java.classLoader, arrayOf(ScreenRepository::class.java)) { _, method, _ ->
            when (method.name) {
                "getHomeListSection" -> {
                    missingListLoads.incrementAndGet()
                    flowOf(BaseResponse(ok = true, data = HomeListSectionModel(listOf(markerless))))
                }
                "getStoreDetailScreen" -> {
                    missingDetailLoads.incrementAndGet()
                    flowOf(BaseResponse(ok = true, data = noMap))
                }
                else -> error("Unexpected missing-location test operation: ${method.name}")
            }
        } as ScreenRepository
        val originalHome = main { replaceRepository(homeVm, fake) }
        val originalDetail = main { replaceRepository(detailVm, fake) }
        try {
            main { homeVm.fetchAroundStores(requestFocusBounds = false) }
            await("markerless card accepted") { homeVm.homeListSection.value.cards.singleOrNull()?.cardId == markerless.cardId }
            main { homeVm.selectHomeListCard(markerless) }
            await("detail without map accepted") { (detailVm.uiState.value as? StoreDetailV2UiState.Content)?.screen == noMap }
            main {
                val setExpanded = HomeFragment::class.java.getDeclaredMethod("setStoreDetailExpanded", Boolean::class.javaPrimitiveType).apply { isAccessible = true }
                val isExpanded = HomeFragment::class.java.getDeclaredMethod("isStoreDetailExpanded").apply { isAccessible = true }
                setExpanded.invoke(home, true)
                HomeFragment::class.java.getDeclaredMethod("moveStorePreviewVisit", Int::class.javaPrimitiveType).apply {
                    isAccessible = true
                }.invoke(home, storeId.toInt())
                check(homeVm.selectedStorePreviewStoreId.value == storeId && homeVm.selectedStoreScreen.value != null && isExpanded.invoke(home) == true) {
                    "Missing-coordinate visit closed Home preview"
                }
            }
            val legacy = main { ViewModelProvider(home.requireActivity())[StoreDetailViewModel::class.java] }
            val repositoryField = legacy.javaClass.getDeclaredField("homeRepository").apply { isAccessible = true }
            val originalReviewRepository = main { repositoryField.get(legacy) }
            var reviewArgs: List<Any?>? = null
            val reviewFake = Proxy.newProxyInstance(HomeRepository::class.java.classLoader, arrayOf(HomeRepository::class.java)) { _, method, args ->
                check(method.name == "postStoreReview") { "Unexpected review operation: ${method.name}" }
                reviewArgs = args?.toList()
                flowOf(BaseResponse<ReviewContentModel>(ok = true, data = null))
            } as HomeRepository
            val listBefore = missingListLoads.get()
            val detailBefore = missingDetailLoads.get()
            try {
                main {
                    repositoryField.set(legacy, reviewFake)
                    legacy.postStoreReview("QA fake review - never sent", 5, storeId.toInt())
                }
                await("fake review saved and refreshed") {
                    missingListLoads.get() > listBefore && missingDetailLoads.get() > detailBefore
                }
                waitForIdleSync()
                main {
                    check(reviewArgs == listOf("QA fake review - never sent", 5, storeId.toInt()))
                    check(missingListLoads.get() == listBefore + 1 && missingDetailLoads.get() == detailBefore + 1)
                    check(homeVm.selectedStorePreviewStoreId.value == storeId)
                }
            } finally {
                main { repositoryField.set(legacy, originalReviewRepository) }
            }
        } finally {
            main {
                replaceRepository(homeVm, originalHome)
                replaceRepository(detailVm, originalDetail)
            }
        }
    }

    private fun verifyFocusPagination(home: HomeFragment, homeVm: HomeViewModel, snapshot: HomeListSectionModel) {
        val template = snapshot.cards.filterIsInstance<HomeListCardModel.BasicCard>().first { it.marker != null }
        val bounds = SDLocationBoundsModel(SDLocationModel(33.2, 126.1), SDLocationModel(33.6, 126.9))
        val cards = listOf(bounds.southWest, bounds.northEast).mapIndexed { index, location ->
            template.copy(
                cardId = "qa-focus-$index",
                marker = requireNotNull(template.marker).copy(location = location, clickLog = null),
                clickLog = null,
                impressionLog = null,
            )
        }
        val firstPage = HomeListSectionModel(cards, SDCursorModel("qa-page-2", true), bounds)
        val nextPage = HomeListSectionModel(
            listOf(template.copy(cardId = "qa-next", clickLog = null, impressionLog = null)),
            SDCursorModel(null, false),
            // Page 2 deliberately carries different bounds: it must not move the camera.
            SDLocationBoundsModel(SDLocationModel(37.0, 126.0), SDLocationModel(38.0, 127.0)),
        )
        val requests = mutableListOf<Array<out Any?>>()
        val fake = Proxy.newProxyInstance(ScreenRepository::class.java.classLoader, arrayOf(ScreenRepository::class.java)) { _, method, args ->
            check(method.name == "getHomeListSection") { "Unexpected camera test operation: ${method.name}" }
            requests += requireNotNull(args).clone()
            flowOf(BaseResponse(ok = true, data = if (args.last() == null) firstPage else nextPage))
        } as ScreenRepository
        val original = main { replaceRepository(homeVm, fake) }
        try {
            main {
                homeVm.closeStorePreview()
                homeVm.fetchAroundStores(requestFocusBounds = true)
            }
            await("focus camera applied") {
                findMap(home.childFragmentManager)?.naverMap?.cameraPosition?.target?.latitude?.let { it in 33.0..34.0 } == true &&
                    homeVm.homeFocusBounds.value == null
            }
            waitForIdleSync()
            val map = main { requireNotNull(findMap(home.childFragmentManager)?.naverMap) }
            val camera = main {
                val view = home.requireView()
                val container = view.findViewById<View>(R.id.container)
                val filter = view.findViewById<View>(R.id.filterComposeView)
                val mapOrigin = IntArray(2).also(container::getLocationInWindow)
                val filterOrigin = IntArray(2).also(filter::getLocationInWindow)
                val sheetHeight = HomeFragment::class.java.getDeclaredField("homeBottomSheetVisibleHeightPx")
                    .apply { isAccessible = true }.getInt(home)
                val top = filterOrigin[1] + filter.height - mapOrigin[1]
                val bottom = container.height - sheetHeight
                listOf(bounds.southWest, bounds.northEast).forEach { location ->
                    val point = map.projection.toScreenLocation(LatLng(location.latitude, location.longitude))
                    check(point.x in 0f..container.width.toFloat() && point.y in top.toFloat()..bottom.toFloat()) {
                        "Focus point $point hidden by controls: visible y=$top..$bottom"
                    }
                }
                map.cameraPosition
            }
            main { homeVm.fetchNextHomeListSection() }
            await("page 2 appended") { homeVm.homeListSection.value.cards.size == cards.size + 1 }
            waitForIdleSync()
            main {
                check(requests.size == 2 && requests[1].last() == "qa-page-2")
                check(requests[0].dropLast(1).toTypedArray().contentDeepEquals(requests[1].dropLast(1).toTypedArray())) {
                    "Page 2 combined the old cursor with a new camera query"
                }
                val after = map.cameraPosition
                check(abs(after.target.latitude - camera.target.latitude) < 1e-7 &&
                    abs(after.target.longitude - camera.target.longitude) < 1e-7 &&
                    abs(after.zoom - camera.zoom) < 1e-6 &&
                    abs(after.tilt - camera.tilt) < 1e-6 && abs(after.bearing - camera.bearing) < 1e-6) {
                    "Pagination moved camera: $camera -> $after"
                }
                check(homeVm.homeFocusBounds.value == null) { "Page 2 published another focus effect" }
            }
        } finally {
            main { replaceRepository(homeVm, original) }
        }
    }

    private fun assertAccountClipboard(host: Any, methodName: String, context: Context) {
        val expected = "테스트은행 123-456"
        host.javaClass.getDeclaredMethod(methodName, StoreDetailV2PlatformAction::class.java).apply {
            isAccessible = true
        }.invoke(host, StoreDetailV2PlatformAction.CopyAccount(expected))
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        check(clipboard.primaryClip?.getItemAt(0)?.text?.toString() == expected) { "Account clipboard mismatch" }
    }

    private fun replaceRepository(owner: Any, repository: ScreenRepository): ScreenRepository {
        val field = owner.javaClass.getDeclaredField("screenRepository").apply { isAccessible = true }
        return (field.get(owner) as ScreenRepository).also { field.set(owner, repository) }
    }

    private fun findHome(manager: FragmentManager): HomeFragment? = manager.fragments.firstNotNullOfOrNull { fragment ->
        if (fragment is HomeFragment) fragment else findHome(fragment.childFragmentManager)
    }

    private fun findMap(manager: FragmentManager): NaverMapFragment? = manager.fragments.firstNotNullOfOrNull { fragment ->
        if (fragment is NaverMapFragment) fragment else findMap(fragment.childFragmentManager)
    }

    private fun await(label: String, predicate: () -> Boolean) {
        val deadline = SystemClock.elapsedRealtime() + 30_000
        while (!main(predicate)) {
            check(SystemClock.elapsedRealtime() < deadline) { "Timed out: $label" }
            SystemClock.sleep(50)
        }
    }

    private fun <T> main(block: () -> T): T {
        var result: Result<T>? = null
        runOnMainSync { result = runCatching(block) }
        return requireNotNull(result).getOrThrow()
    }
}
