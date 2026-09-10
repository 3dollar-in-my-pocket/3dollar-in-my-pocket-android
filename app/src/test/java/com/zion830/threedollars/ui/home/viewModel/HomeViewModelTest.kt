package com.zion830.threedollars.ui.home.viewModel

import androidx.lifecycle.SavedStateHandle
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterRadioOption
import com.threedollar.common.serverdriven.model.HomeFilterScreenModel
import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.HomeListStoreReferenceModel
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.common.serverdriven.model.HomeScreenSectionType
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDCursorModel
import com.threedollar.common.serverdriven.model.SDLocationBoundsModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.screen.repository.ScreenRepository
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem
import com.zion830.threedollars.ui.home.data.HomeUIState
import java.io.IOException
import java.lang.reflect.Proxy
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.BeforeClass

class HomeViewModelTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun useServiceLoadedMainDispatcher() {
            System.setProperty("kotlinx.coroutines.fast.service.loader", "false")
        }
    }

    @Test
    fun `filter gate waits and submits only latest location and category`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = viewModel(repository)
        val category = StoreCategoryItem("CATEGORY", "분식", "", "", "", false)

        viewModel.fetchAroundStores(LatLng(37.1, 127.1))
        viewModel.fetchAroundStores(LatLng(37.2, 127.2))
        viewModel.setCategoryForTest(category)
        viewModel.fetchAroundStores()

        assertEquals(0, repository.listCalls.size)
        repository.respondFilter(0, success(filterScreen(sortValues = listOf("DISTANCE", "POPULAR"))))
        await { repository.listCalls.size == 1 }

        val request = repository.listCalls.single().request
        assertEquals(37.2, request.mapLatitude, 0.0)
        assertEquals(127.2, request.mapLongitude, 0.0)
        assertEquals(listOf("CATEGORY"), request.categoryIds?.toList())
        assertEquals("DISTANCE", request.dynamicParams["sortType"])
        repository.respondList(0, success(HomeListSectionModel()))
    }

    @Test
    fun `server page view is queued once with extras across filter retries`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = viewModel(repository)
        val viewLog = SDViewLogModel(
            screenName = "HOME_SERVER",
            extraParameters = mapOf("SOURCE" to SDClickLogValue.StringValue("filter")),
        )

        viewModel.requestHomePageView()
        repository.respondFilter(0, success(filterScreen().copy(viewLog = viewLog)))
        assertEquals(viewLog, withTimeout(1_000) { viewModel.homePageViewEvent.first() }.serverLog)

        viewModel.retryHomeFilterScreen()
        await { repository.filterCalls.size == 2 }
        repository.respondFilter(1, success(filterScreen().copy(viewLog = viewLog)))
        assertNull(withTimeoutOrNull(50) { viewModel.homePageViewEvent.first() })

        viewModel.requestHomePageView()
        assertEquals(viewLog, withTimeout(1_000) { viewModel.homePageViewEvent.first() }.serverLog)
    }

    @Test
    fun `filter failure releases latest list with popular sort and retry success refreshes once`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = viewModel(repository)

        viewModel.fetchAroundStores(LatLng(37.3, 127.3))
        repository.respondFilter(0, BaseResponse(ok = false, data = null))
        await { repository.listCalls.size == 1 }
        assertEquals("POPULAR", repository.listCalls[0].request.dynamicParams["sortType"])
        repository.respondList(0, success(HomeListSectionModel()))

        viewModel.updateMapPosition(LatLng(37.4, 127.4))
        assertEquals(true, viewModel.retryHomeFilterScreenIfFailed())
        await { repository.filterCalls.size == 2 }
        val recovered = filterScreen(sortValues = listOf("POPULAR", "DISTANCE"))
        repository.respondFilter(1, success(recovered))
        await { repository.listCalls.size == 2 }
        assertEquals(37.4, repository.listCalls[1].request.mapLatitude, 0.0)
        repository.respondList(1, success(HomeListSectionModel()))

        assertEquals(false, viewModel.retryHomeFilterScreenIfFailed())

        viewModel.retryHomeFilterScreen()
        await { repository.filterCalls.size == 3 }
        repository.respondFilter(2, success(recovered))
        yield()
        assertEquals(2, repository.listCalls.size)
    }

    @Test
    fun `new first page cancels stale request and only latest response mutates cards`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)

        repository.ignoreCancellationForNextList = true
        viewModel.fetchAroundStores(LatLng(37.1, 127.1))
        await { repository.listCalls.size == 1 }
        viewModel.fetchAroundStores(LatLng(37.2, 127.2))
        await { repository.listCalls.size == 2 }

        repository.respondList(0, BaseResponse(ok = false, data = null, message = "stale error"))
        await { repository.listCalls[0].completed && repository.listCalls[0].cancelled }
        assertEquals(true, viewModel.isHomeListLoadingForTest())
        repository.respondList(1, success(HomeListSectionModel(cards = listOf(card("B", 2)))))
        await { viewModel.homeListSection.value.cards.isNotEmpty() }

        assertEquals(listOf("B"), viewModel.homeListSection.value.cards.map { it.cardId })
        assertEquals(false, viewModel.isHomeListLoadingForTest())
    }

    @Test
    fun `late filter attempt cannot resolve gate or overwrite latest configuration`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = viewModel(repository)
        viewModel.fetchAroundStores()

        viewModel.retryHomeFilterScreen()
        await { repository.filterCalls.size == 2 }
        repository.respondFilter(0, success(filterScreen(sortValues = listOf("DISTANCE"))))
        yield()
        assertEquals(false, viewModel.uiState.value.hasResolvedFilterScreen)
        assertEquals(0, repository.listCalls.size)

        repository.respondFilter(1, success(filterScreen(sortValues = listOf("POPULAR"))))
        await { repository.listCalls.size == 1 }
        assertEquals("POPULAR", repository.listCalls.single().request.dynamicParams["sortType"])
        repository.respondList(0, success(HomeListSectionModel()))
    }

    @Test
    fun `pagination reuses first page query snapshot after map state changes`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)

        viewModel.updateDistanceM(1_000.0)
        viewModel.fetchAroundStores(
            mapPosition = LatLng(37.1, 127.1),
            userLocation = LatLng(37.0, 127.0),
        )
        await { repository.listCalls.size == 1 }
        repository.respondList(
            0,
            success(HomeListSectionModel(cards = listOf(card("A", 1)), cursor = SDCursorModel("next", true))),
        )
        await { viewModel.homeListSection.value.cards.size == 1 }

        viewModel.updateMapPosition(LatLng(38.0, 128.0))
        viewModel.updateUserLocation(LatLng(38.1, 128.1))
        viewModel.updateDistanceM(9_000.0)
        viewModel.fetchNextHomeListSection()
        await { repository.listCalls.size == 2 }

        val page1 = repository.listCalls[0].request
        val page2 = repository.listCalls[1].request
        assertEquals(page1.copy(cursor = "next"), page2)
        repository.respondList(1, success(HomeListSectionModel(cards = listOf(card("B", 2)))))
    }

    @Test
    fun `duplicate cursor is blocked and failed page can retry`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)
        viewModel.fetchAroundStores()
        await { repository.listCalls.size == 1 }
        repository.respondList(0, success(HomeListSectionModel(cursor = SDCursorModel("next", true))))
        await { viewModel.homeListSection.value.cursor?.nextCursor == "next" }

        viewModel.fetchNextHomeListSection()
        viewModel.fetchNextHomeListSection()
        await { repository.listCalls.size == 2 }
        assertEquals(2, repository.listCalls.size)

        repository.respondList(1, BaseResponse(ok = false, data = null, message = "failed"))
        await { repository.listCalls[1].completed }
        viewModel.fetchNextHomeListSection()
        await { repository.listCalls.size == 3 }
        repository.respondList(2, success(HomeListSectionModel()))
    }

    @Test
    fun `new first page cancels page two and blocks its late append`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)
        viewModel.fetchAroundStores()
        await { repository.listCalls.size == 1 }
        repository.respondList(
            0,
            success(HomeListSectionModel(cards = listOf(card("A", 1)), cursor = SDCursorModel("next", true))),
        )
        await { viewModel.homeListSection.value.cards.map { it.cardId } == listOf("A") }

        viewModel.fetchNextHomeListSection()
        await { repository.listCalls.size == 2 }
        viewModel.fetchAroundStores()
        await { repository.listCalls.size == 3 && repository.listCalls[1].cancelled }
        repository.respondList(2, success(HomeListSectionModel(cards = listOf(card("B", 2)))))
        repository.respondList(1, success(HomeListSectionModel(cards = listOf(card("old-page", 3)))))
        await { viewModel.homeListSection.value.cards.map { it.cardId } == listOf("B") }

        assertEquals(listOf("B"), viewModel.homeListSection.value.cards.map { it.cardId })
    }

    @Test
    fun `focus response remains available when there was no active collector`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)
        val bounds = SDLocationBoundsModel(SDLocationModel(37.0, 127.0), SDLocationModel(38.0, 128.0))

        viewModel.fetchAroundStores(requestFocusBounds = true)
        await { repository.listCalls.size == 1 }
        repository.respondList(0, success(HomeListSectionModel(focusBounds = bounds)))
        await { viewModel.homeListSection.value.focusBounds == bounds }

        val effect = withTimeoutOrNull(100) { viewModel.homeFocusBounds.first() }
        assertEquals(bounds, effect?.bounds)
    }

    @Test
    fun `focus is invalidated by a newer request before viewport consumption`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)
        val bounds = SDLocationBoundsModel(SDLocationModel(37.0, 127.0), SDLocationModel(38.0, 128.0))
        viewModel.fetchAroundStores(requestFocusBounds = true)
        await { repository.listCalls.size == 1 }
        repository.respondList(0, success(HomeListSectionModel(focusBounds = bounds)))
        await { viewModel.homeFocusBounds.value != null }
        val staleEffect = requireNotNull(viewModel.homeFocusBounds.value)

        viewModel.fetchAroundStores(requestFocusBounds = false)

        assertNull(viewModel.homeFocusBounds.value)
        assertEquals(false, viewModel.isHomeFocusBoundsCurrent(staleEffect))
        repository.respondList(1, success(HomeListSectionModel()))
    }

    @Test
    fun `gesture after request prevents its late focus and consumed focus is not replayed`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)
        val bounds = SDLocationBoundsModel(SDLocationModel(37.0, 127.0), SDLocationModel(38.0, 128.0))
        viewModel.fetchAroundStores(requestFocusBounds = true)
        await { repository.listCalls.size == 1 }
        viewModel.onHomeMapGesture()
        repository.respondList(0, success(HomeListSectionModel(focusBounds = bounds)))
        await { repository.listCalls[0].completed }
        assertNull(viewModel.homeFocusBounds.value)

        viewModel.fetchAroundStores(requestFocusBounds = true)
        await { repository.listCalls.size == 2 }
        repository.respondList(1, success(HomeListSectionModel(focusBounds = bounds)))
        await { viewModel.homeFocusBounds.value != null }
        val effect = requireNotNull(viewModel.homeFocusBounds.value)
        viewModel.consumeHomeFocusBounds(effect)

        assertNull(viewModel.homeFocusBounds.value)
        assertNull(withTimeoutOrNull(50) { viewModel.homeFocusBounds.first { it != null } })
    }

    @Test
    fun `gesture while focus request is waiting for filter invalidates that intent`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = viewModel(repository)
        val bounds = SDLocationBoundsModel(SDLocationModel(37.0, 127.0), SDLocationModel(38.0, 128.0))
        viewModel.fetchAroundStores(requestFocusBounds = true)

        viewModel.onHomeMapGesture()
        repository.respondFilter(0, success(filterScreen()))
        await { repository.listCalls.size == 1 }
        repository.respondList(0, success(HomeListSectionModel(focusBounds = bounds)))
        await { repository.listCalls[0].completed }

        assertNull(viewModel.homeFocusBounds.value)
    }

    @Test
    fun `filter IOException releases fallback while cancellation keeps gate closed`() = runBlocking {
        val failedRepository = FakeScreenRepository(filterFailure = IOException("offline"))
        val failedViewModel = viewModel(failedRepository)
        failedViewModel.fetchAroundStores()
        await { failedRepository.listCalls.size == 1 }
        assertEquals("POPULAR", failedRepository.listCalls.single().request.dynamicParams["sortType"])
        failedRepository.respondList(0, success(HomeListSectionModel()))

        val cancelledRepository = FakeScreenRepository(filterFailure = CancellationException("cancel"))
        val cancelledViewModel = viewModel(cancelledRepository)
        cancelledViewModel.fetchAroundStores()
        yield()
        assertEquals(0, cancelledRepository.listCalls.size)
    }

    @Test
    fun `refresh keeps selection made while response is pending`() = runBlocking {
        val repository = FakeScreenRepository()
        val viewModel = readyViewModel(repository)
        val first = card("A", 1)
        val second = card("B", 2)
        viewModel.fetchAroundStores()
        await { repository.listCalls.size == 1 }
        repository.respondList(0, success(HomeListSectionModel(cards = listOf(first, second))))
        await { viewModel.homeListSection.value.cards.size == 2 }
        viewModel.selectHomeListCard(first)

        viewModel.refreshHomeListSectionAfterStoreUpdate()
        await { repository.listCalls.size == 2 }
        viewModel.selectHomeListCard(second)
        repository.respondList(1, success(HomeListSectionModel(cards = listOf(first, second))))
        await { repository.listCalls[1].completed }

        assertEquals("B", viewModel.selectedHomeListCardId.value)
        assertEquals(2L, viewModel.selectedStorePreviewStoreId.value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun HomeViewModel.setCategoryForTest(category: StoreCategoryItem) {
        val field = HomeViewModel::class.java.getDeclaredField("_uiState").apply { isAccessible = true }
        (field.get(this) as MutableStateFlow<HomeUIState>).update { it.copy(selectedCategory = category) }
    }

    private fun HomeViewModel.isHomeListLoadingForTest(): Boolean {
        val field = HomeViewModel::class.java.getDeclaredField("isHomeListLoading").apply { isAccessible = true }
        return field.getBoolean(this)
    }

    private suspend fun readyViewModel(repository: FakeScreenRepository): HomeViewModel {
        val viewModel = viewModel(repository)
        repository.respondFilter(0, success(filterScreen()))
        await { viewModel.uiState.value.hasResolvedFilterScreen }
        return viewModel
    }

    private fun viewModel(repository: FakeScreenRepository) = HomeViewModel(
        homeRepository = unusedHomeRepository(),
        screenRepository = repository,
        savedStateHandle = SavedStateHandle(),
    )

    private suspend fun await(condition: () -> Boolean) {
        withTimeout(2_000) {
            while (!condition()) yield()
        }
    }

    private fun filterScreen(sortValues: List<String> = listOf("POPULAR")) = HomeFilterScreenModel(
        sections = listOf(
            HomeScreenSection.HomeFilterSectionModel(
                type = HomeScreenSectionType.HOME_FILTER,
                bars = listOf(
                    HomeFilterBar.RadioBar(
                        type = HomeFilterBarType.RADIO_BAR,
                        paramKey = "sortType",
                        options = sortValues.map { value ->
                            HomeFilterRadioOption(
                                chip = SDChipModel(text = SDTextModel(value, false)),
                                paramValue = value,
                                clickLog = null,
                            )
                        },
                    )
                ),
            )
        ),
    )

    private fun card(id: String, storeId: Long) = HomeListCardModel.BasicCard(
        type = "BASIC_CARD",
        cardId = id,
        header = HomeListCardHeaderModel(),
        metadata = HomeListCardMetadataModel(),
        refs = listOf(HomeListStoreReferenceModel("STORE", storeId.toString(), "USER_STORE")),
    )

    private fun <T> success(data: T) = BaseResponse(ok = true, data = data)
}

private data class HomeListRequest(
    val distanceM: Double,
    val categoryIds: Array<String>?,
    val targetStores: Array<String>?,
    val mapLatitude: Double,
    val mapLongitude: Double,
    val deviceLatitude: Double,
    val deviceLongitude: Double,
    val dynamicParams: Map<String, String>,
    val cursor: String?,
) {
    override fun equals(other: Any?): Boolean = other is HomeListRequest &&
        distanceM == other.distanceM && categoryIds.contentEqualsNullable(other.categoryIds) &&
        targetStores.contentEqualsNullable(other.targetStores) && mapLatitude == other.mapLatitude &&
        mapLongitude == other.mapLongitude && deviceLatitude == other.deviceLatitude &&
        deviceLongitude == other.deviceLongitude && dynamicParams == other.dynamicParams && cursor == other.cursor

    override fun hashCode(): Int = listOf(
        distanceM, categoryIds?.contentHashCode(), targetStores?.contentHashCode(), mapLatitude,
        mapLongitude, deviceLatitude, deviceLongitude, dynamicParams, cursor,
    ).hashCode()
}

private data class ListCall(
    val request: HomeListRequest,
    val ignoresCancellation: Boolean,
    val responses: Channel<BaseResponse<HomeListSectionModel>> = Channel(Channel.UNLIMITED),
    @Volatile var lateContinuation: Continuation<BaseResponse<HomeListSectionModel>>? = null,
    @Volatile var cancelled: Boolean = false,
    @Volatile var completed: Boolean = false,
)

private class FakeScreenRepository(
    private val filterFailure: Throwable? = null,
) : ScreenRepository {
    val filterCalls = CopyOnWriteArrayList<Channel<BaseResponse<HomeFilterScreenModel>>>()
    val listCalls = CopyOnWriteArrayList<ListCall>()
    @Volatile var ignoreCancellationForNextList: Boolean = false

    override fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenModel>> = flow {
        val responses = Channel<BaseResponse<HomeFilterScreenModel>>(Channel.UNLIMITED)
        filterCalls += responses
        filterFailure?.let { throw it }
        emit(responses.receive())
    }

    override fun getHomeListSection(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
        dynamicParams: Map<String, String>,
        cursor: String?,
    ): Flow<BaseResponse<HomeListSectionModel>> = object : Flow<BaseResponse<HomeListSectionModel>> {
        override suspend fun collect(collector: FlowCollector<BaseResponse<HomeListSectionModel>>) {
        val ignoresCancellation = ignoreCancellationForNextList.also { ignoreCancellationForNextList = false }
        val call = ListCall(
            request = HomeListRequest(
                distanceM, categoryIds, targetStores, mapLatitude, mapLongitude,
                deviceLatitude, deviceLongitude, dynamicParams, cursor,
            ),
            ignoresCancellation = ignoresCancellation,
        )
        listCalls += call
        try {
            currentCoroutineContext()[Job]?.invokeOnCompletion { cause ->
                if (cause is CancellationException) call.cancelled = true
            }
            val response = if (call.ignoresCancellation) {
                suspendCoroutine { continuation -> call.lateContinuation = continuation }
            } else {
                call.responses.receive()
            }
            collector.emit(response)
        } catch (cancellation: CancellationException) {
            call.cancelled = true
            throw cancellation
        } finally {
            call.completed = true
        }
        }
    }

    fun respondFilter(index: Int, response: BaseResponse<HomeFilterScreenModel>) {
        filterCalls[index].trySend(response).getOrThrow()
    }

    fun respondList(index: Int, response: BaseResponse<HomeListSectionModel>) {
        val call = listCalls[index]
        val continuation = call.lateContinuation
        if (continuation != null) {
            call.lateContinuation = null
            continuation.resume(response)
        } else {
            call.responses.trySend(response).getOrThrow()
        }
    }

    override fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<SDScreenModel>> = error("unused")
    override fun getStoreContributorHistories(storeId: String, cursor: String?): Flow<BaseResponse<SDSectionModel.CardsSection>> = error("unused")
    override fun getStoreDetailScreen(storeId: Long, deviceLatitude: Double?, deviceLongitude: Double?): Flow<BaseResponse<StoreDetailScreenModel>> = error("unused")
    override fun putStorePostStickers(storeId: Long, postId: Long, stickers: List<String>) = flowOf(BaseResponse(ok = true, data = true))
    override fun issueStoreCoupon(storeId: Long, couponId: String) = flowOf(BaseResponse(ok = true, data = true))
    override fun useIssuedCoupon(issuedKey: String) = flowOf(BaseResponse(ok = true, data = true))
    override fun deleteStoreReview(reviewId: Long) = flowOf(BaseResponse(ok = true, data = true))
}

private fun unusedHomeRepository(): HomeRepository = Proxy.newProxyInstance(
    HomeRepository::class.java.classLoader,
    arrayOf(HomeRepository::class.java),
) { _, method, _ -> error("Unexpected HomeRepository call: ${method.name}") } as HomeRepository

private fun Array<String>?.contentEqualsNullable(other: Array<String>?): Boolean = when {
    this == null && other == null -> true
    this == null || other == null -> false
    else -> contentEquals(other)
}
