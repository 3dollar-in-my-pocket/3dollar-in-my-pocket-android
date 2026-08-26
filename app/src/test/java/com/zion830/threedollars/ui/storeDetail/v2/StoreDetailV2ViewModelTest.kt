package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailContentModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.screen.repository.ScreenRepository
import java.lang.reflect.Proxy
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailV2ViewModelTest {

    @Test
    fun changingStoreCancelsOldLoadAndOnlyPublishesLatestStoreContent() = runBlocking {
        val repository = DeferredScreenRepository()
        val viewModel = StoreDetailV2ViewModel(repository, unusedHomeRepository())

        viewModel.load(storeId = 1L, deviceLatitude = null, deviceLongitude = null)
        viewModel.load(storeId = 2L, deviceLatitude = 37.5, deviceLongitude = 127.0)
        repository.respond(storeId = 1L, response = successResponse("old"))
        repository.respond(storeId = 2L, response = successResponse("latest"))

        val content = withTimeout(3_000) {
            viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Content>().first()
        }
        assertEquals(2L, content.storeId)
        assertEquals("latest", (content.screen.sections.single() as StoreDetailSectionModel.Cta).content.title.text)
        assertEquals(37.5, repository.latitudes[2L])
        assertEquals(127.0, repository.longitudes[2L])
    }

    @Test
    fun notExistsStoreLoadFailurePublishesErrorMessageAndCloseContainerEvent() = runBlocking {
        val repository = DeferredScreenRepository()
        val viewModel = StoreDetailV2ViewModel(repository, unusedHomeRepository())
        val closeEvent = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(3_000) {
                viewModel.events.filterIsInstance<StoreDetailV2Event.CloseContainer>().first()
            }
        }

        viewModel.load(storeId = 3L, deviceLatitude = null, deviceLongitude = null)
        repository.respond(
            storeId = 3L,
            response = BaseResponse(
                ok = false,
                data = null,
                message = "삭제된 가게입니다",
                error = "not_exists_store",
            ),
        )

        val error = withTimeout(3_000) {
            viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Error>().first()
        }
        assertEquals("삭제된 가게입니다", error.message)
        assertEquals("삭제된 가게입니다", closeEvent.await().message)
    }

    @Test
    fun mutationFailureKeepsContentAndSuccessOrChildResultRefreshesAndMarksUpdated() = runBlocking {
        val repository = DeferredScreenRepository()
        val viewModel = StoreDetailV2ViewModel(repository, unusedHomeRepository())
        viewModel.load(storeId = 4L, deviceLatitude = null, deviceLongitude = null)
        repository.respond(4L, successResponse("initial"))
        withTimeout(3_000) {
            viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Content>().first()
        }

        repository.mutationResponse = BaseResponse(ok = false, data = false, message = "쿠폰 발급 실패")
        val failureMessage = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(3_000) { viewModel.events.filterIsInstance<StoreDetailV2Event.ShowMessage>().first() }
        }
        viewModel.issueCoupon("coupon-1")

        assertEquals("쿠폰 발급 실패", failureMessage.await().message)
        assertEquals("initial", viewModel.currentContentTitle())
        assertEquals(1, repository.loadCount)

        repository.mutationResponse = BaseResponse(ok = true, data = true)
        viewModel.issueCoupon("coupon-1")
        withTimeout(3_000) { while (repository.loadCount < 2) yield() }
        assertEquals("initial", viewModel.currentContentTitle())
        repository.respond(4L, successResponse("after-mutation"))
        withTimeout(3_000) {
            viewModel.uiState
                .filterIsInstance<StoreDetailV2UiState.Content>()
                .filter { it.title() == "after-mutation" }
                .first()
        }
        assertEquals(true, viewModel.hasUpdates.value)

        viewModel.onChildResult(updated = true)
        withTimeout(3_000) { while (repository.loadCount < 3) yield() }
        repository.respond(4L, successResponse("after-child"))
        withTimeout(3_000) {
            viewModel.uiState
                .filterIsInstance<StoreDetailV2UiState.Content>()
                .filter { it.title() == "after-child" }
                .first()
        }
        Unit
    }

    @Test
    fun repositoryExceptionLeavesLoadingAndPublishesFallbackMessage() = runBlocking {
        val repository = DeferredScreenRepository().apply {
            loadFailure = IOException("offline")
        }
        val viewModel = StoreDetailV2ViewModel(repository, unusedHomeRepository())
        val messageEvent = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(3_000) {
                viewModel.events.filterIsInstance<StoreDetailV2Event.ShowMessage>().first()
            }
        }

        viewModel.load(storeId = 6L, deviceLatitude = null, deviceLongitude = null)

        val error = withTimeout(3_000) {
            viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Error>().first()
        }
        assertEquals(6L, error.storeId)
        assertEquals(null, error.message)
        assertEquals(null, messageEvent.await().message)
    }

    @Test
    fun refreshExceptionKeepsContentAndFavoriteResultUsesSuccessfulLocalMutation() = runBlocking {
        val repository = DeferredScreenRepository()
        val viewModel = StoreDetailV2ViewModel(
            repository,
            favoriteHomeRepository(BaseResponse(ok = true, data = true)),
        )
        viewModel.load(7L, null, null)
        repository.respond(7L, successResponse("initial"))
        withTimeout(3_000) {
            viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Content>().first()
        }
        repository.loadFailure = IOException("refresh failed")
        val messageEvent = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(3_000) {
                viewModel.events.filterIsInstance<StoreDetailV2Event.ShowMessage>().first()
            }
        }

        viewModel.toggleFavorite(isFavorite = false)

        withTimeout(3_000) {
            while (viewModel.favoriteOverride.value != true) yield()
        }
        assertEquals("initial", viewModel.currentContentTitle())
        assertEquals(true, viewModel.hasUpdates.value)
        assertEquals(null, messageEvent.await().message)
    }

    @Test
    fun unchangedChildResultDoesNotRefreshOrMarkStoreUpdated() = runBlocking {
        val repository = DeferredScreenRepository()
        val viewModel = StoreDetailV2ViewModel(repository, unusedHomeRepository())
        viewModel.load(8L, null, null)
        repository.respond(8L, successResponse("initial"))
        withTimeout(3_000) {
            viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Content>().first()
        }

        viewModel.onChildResult(updated = false)

        yield()
        assertEquals(1, repository.loadCount)
        assertEquals(false, viewModel.hasUpdates.value)
    }

    @Test
    fun backgroundLoadDoesNotLogViewUntilSharedContentIsDisplayed() = runBlocking {
        val repository = DeferredScreenRepository()
        val viewModel = StoreDetailV2ViewModel(repository, unusedHomeRepository())
        viewModel.load(9L, null, null)
        repository.respond(9L, successResponse("loaded-in-background"))
        val content = withTimeout(3_000) {
            viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Content>().first()
        }

        assertEquals(null, viewModel.viewLoggedStoreId)
        viewModel.sendViewLog(content.screen.viewLog)
        assertEquals(9L, viewModel.viewLoggedStoreId)
    }

    @Test
    fun linkWinsAndPlatformCustomActionsMapToTypedHostEvents() = runBlocking {
        val viewModel = StoreDetailV2ViewModel(DeferredScreenRepository(), unusedHomeRepository())
        val linkEvent = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(3_000) { viewModel.events.filterIsInstance<StoreDetailV2Event.Platform>().first() }
        }
        viewModel.onAction(
            actionBar(
                actionType = "STORE_PREVIEW_SECTION_SHARE",
                link = SDLinkModel(type = "WEB", link = "https://example.com"),
            )
        )
        assertEquals(
            "https://example.com",
            (linkEvent.await().action as StoreDetailV2PlatformAction.OpenLink).link.link,
        )

        val cases = listOf(
            "STORE_PREVIEW_SECTION_SHARE" to StoreDetailV2PlatformAction.Share::class.java,
            "STORE_PREVIEW_SECTION_NAVIGATION" to StoreDetailV2PlatformAction.Navigation::class.java,
            "STORE_PREVIEW_SECTION_REVIEW_WRITE" to StoreDetailV2PlatformAction.ReviewWrite::class.java,
            "STORE_EDIT_SECTION_UPDATE" to StoreDetailV2PlatformAction.EditStore::class.java,
            "STORE_EDIT_SECTION_REPORT" to StoreDetailV2PlatformAction.ReportStore::class.java,
            "STORE_IMAGE_SECTION_ADD_IMAGE" to StoreDetailV2PlatformAction.AddImage::class.java,
            "STORE_IMAGE_SECTION_IMAGE_ENLARGE" to StoreDetailV2PlatformAction.EnlargeImage::class.java,
            "STORE_REVIEW_SECTION_REVIEW_WRITE" to StoreDetailV2PlatformAction.ReviewWrite::class.java,
            "STORE_REVIEW_SECTION_REPORT" to StoreDetailV2PlatformAction.ReportReview::class.java,
            "STORE_MAP_SECTION_COPY_ADDRESS" to StoreDetailV2PlatformAction.CopyAddress::class.java,
            "STORE_MAP_SECTION_MAP_ENLARGE" to StoreDetailV2PlatformAction.EnlargeMap::class.java,
        )
        cases.forEach { (actionType, expectedClass) ->
            val event = async(start = CoroutineStart.UNDISPATCHED) {
                withTimeout(3_000) { viewModel.events.filterIsInstance<StoreDetailV2Event.Platform>().first() }
            }
            viewModel.onAction(actionBar(actionType))
            assertEquals(expectedClass, event.await().action.javaClass)
        }
    }

    @Test
    fun mutationCustomActionsRouteToExistingAndV2RepositoriesWithExactIdentifiers() = runBlocking {
        val repository = DeferredScreenRepository().apply {
            mutationResponse = BaseResponse(ok = false, data = false, message = "expected test failure")
        }
        val homeTracker = HomeMutationTracker()
        val viewModel = StoreDetailV2ViewModel(repository, trackingHomeRepository(homeTracker))
        viewModel.load(5L, null, null)
        repository.respond(5L, successResponse("initial"))
        withTimeout(3_000) { viewModel.uiState.filterIsInstance<StoreDetailV2UiState.Content>().first() }

        viewModel.onAction(actionBar("STORE_COUPON_SECTION_COUPON_ISSUE", extraParams = mapOf("COUPON_ID" to SDClickLogValue.StringValue("coupon"))))
        viewModel.onAction(actionBar("STORE_COUPON_SECTION_COUPON_USE", extraParams = mapOf("ISSUED_KEY" to SDClickLogValue.StringValue("issued"))))
        viewModel.onAction(actionBar("STORE_POST_SECTION_ADD_LIKE", extraParams = mapOf("POST_ID" to SDClickLogValue.LongValue(7), "STICKER_ID" to SDClickLogValue.StringValue("like"))))
        viewModel.onAction(actionBar("STORE_POST_SECTION_CANCEL_LIKE", extraParams = mapOf("POST_ID" to SDClickLogValue.LongValue(7), "STICKER_ID" to SDClickLogValue.StringValue(""))))
        viewModel.onAction(actionBar("STORE_REVIEW_SECTION_DELETE", extraParams = mapOf("REVIEW_ID" to SDClickLogValue.LongValue(9))))
        viewModel.onAction(actionBar("STORE_REVIEW_SECTION_ADD_LIKE", extraParams = mapOf("REVIEW_ID" to SDClickLogValue.LongValue(9), "STICKER_ID" to SDClickLogValue.StringValue("like"))))
        viewModel.onAction(actionBar("STORE_REVIEW_SECTION_CANCEL_LIKE", extraParams = mapOf("REVIEW_ID" to SDClickLogValue.LongValue(9), "STICKER_ID" to SDClickLogValue.StringValue(""))))

        withTimeout(3_000) {
            while (repository.postStickerCalls.size < 2 || homeTracker.reviewStickerCalls.size < 2 || repository.deletedReviewId == null) yield()
        }
        assertEquals("coupon", repository.issuedCouponId)
        assertEquals("issued", repository.usedIssuedKey)
        assertEquals(listOf("like"), repository.postStickerCalls[0].third)
        assertEquals(listOf(""), repository.postStickerCalls[1].third)
        assertEquals(9L, repository.deletedReviewId)
        assertEquals(listOf("like"), homeTracker.reviewStickerCalls[0].third)
        assertEquals(listOf(""), homeTracker.reviewStickerCalls[1].third)
    }

    private fun successResponse(title: String): BaseResponse<StoreDetailScreenModel> = BaseResponse(
        ok = true,
        data = StoreDetailScreenModel(
            sections = listOf(
                StoreDetailSectionModel.Cta(
                    type = "CTA",
                    content = StoreDetailContentModel(
                        title = SDTextModel(text = title, isHtml = false),
                    ),
                ),
            ),
            viewLog = SDViewLogModel(screenName = "store_detail"),
        ),
    )

    private fun actionBar(
        actionType: String,
        link: SDLinkModel? = null,
        extraParams: Map<String, SDClickLogValue> = emptyMap(),
    ) = StoreActionBarModel(
        type = "ACTION",
        button = SDButtonModel(
            text = SDTextModel(text = actionType, isHtml = false),
            link = link,
            customAction = SDCustomActionModel(actionType = actionType, extraParams = extraParams),
        ),
    )
}

private fun StoreDetailV2ViewModel.currentContentTitle(): String =
    (uiState.value as StoreDetailV2UiState.Content).title()

private fun StoreDetailV2UiState.Content.title(): String =
    (screen.sections.single() as StoreDetailSectionModel.Cta).content.title.text

private class DeferredScreenRepository : ScreenRepository {
    private val responses = ConcurrentHashMap<Long, Channel<BaseResponse<StoreDetailScreenModel>>>()
    val latitudes = ConcurrentHashMap<Long, Double?>()
    val longitudes = ConcurrentHashMap<Long, Double?>()
    var loadCount: Int = 0
    var loadFailure: Throwable? = null
    var mutationResponse: BaseResponse<Boolean> = BaseResponse(ok = true, data = true)
    val postStickerCalls = mutableListOf<Triple<Long, Long, List<String>>>()
    var issuedCouponId: String? = null
    var usedIssuedKey: String? = null
    var deletedReviewId: Long? = null

    fun respond(storeId: Long, response: BaseResponse<StoreDetailScreenModel>) {
        responses.getOrPut(storeId) { Channel(Channel.UNLIMITED) }.trySend(response).getOrThrow()
    }

    override fun getStoreDetailScreen(
        storeId: Long,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Flow<BaseResponse<StoreDetailScreenModel>> = flow {
        loadCount += 1
        loadFailure?.let { throw it }
        deviceLatitude?.let { latitudes[storeId] = it }
        deviceLongitude?.let { longitudes[storeId] = it }
        emit(responses.getOrPut(storeId) { Channel(Channel.UNLIMITED) }.receive())
    }

    override fun putStorePostStickers(storeId: Long, postId: Long, stickers: List<String>): Flow<BaseResponse<Boolean>> =
        flowOf(mutationResponse).also { postStickerCalls += Triple(storeId, postId, stickers) }

    override fun issueStoreCoupon(storeId: Long, couponId: String): Flow<BaseResponse<Boolean>> =
        flowOf(mutationResponse).also { issuedCouponId = couponId }
    override fun useIssuedCoupon(issuedKey: String): Flow<BaseResponse<Boolean>> =
        flowOf(mutationResponse).also { usedIssuedKey = issuedKey }
    override fun deleteStoreReview(reviewId: Long): Flow<BaseResponse<Boolean>> =
        flowOf(mutationResponse).also { deletedReviewId = reviewId }
    private fun successfulMutation() = flowOf(BaseResponse(ok = true, data = true))

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
    ) = error("unused")

    override fun getStoreContributorScreen(storeId: String) = error("unused")
    override fun getStoreContributorHistories(storeId: String, cursor: String?) = error("unused")
    override fun getHomeFilterScreen() = error("unused")
}

private fun unusedHomeRepository(): HomeRepository = Proxy.newProxyInstance(
    HomeRepository::class.java.classLoader,
    arrayOf(HomeRepository::class.java),
) { _, method, _ -> error("Unexpected HomeRepository call: ${method.name}") } as HomeRepository

private fun favoriteHomeRepository(response: BaseResponse<Boolean>): HomeRepository = Proxy.newProxyInstance(
    HomeRepository::class.java.classLoader,
    arrayOf(HomeRepository::class.java),
) { _, method, _ ->
    when (method.name) {
        "putFavorite", "deleteFavorite" -> flowOf(response)
        else -> error("Unexpected HomeRepository call: ${method.name}")
    }
} as HomeRepository

private data class HomeMutationTracker(
    val reviewStickerCalls: MutableList<Triple<String, String, List<String>>> = mutableListOf(),
)

private fun trackingHomeRepository(tracker: HomeMutationTracker): HomeRepository = Proxy.newProxyInstance(
    HomeRepository::class.java.classLoader,
    arrayOf(HomeRepository::class.java),
) { _, method, args ->
    when (method.name) {
        "putStickers" -> {
            @Suppress("UNCHECKED_CAST")
            tracker.reviewStickerCalls += Triple(args[0] as String, args[1] as String, args[2] as List<String>)
            flowOf(BaseResponse(ok = false, data = null, message = "expected test failure"))
        }
        else -> error("Unexpected HomeRepository call: ${method.name}")
    }
} as HomeRepository
