package com.zion830.threedollars.ui.feed.viewModel

import com.threedollar.common.analytics.SDLogSender
import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsSectionModel
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.domain.feed.repository.FeedRepository
import com.threedollar.domain.screen.repository.ScreenRepository
import com.zion830.threedollars.ui.feed.model.FeedCardImpressionDedup
import com.zion830.threedollars.ui.feed.model.FeedImageViewerRequest
import com.zion830.threedollars.ui.feed.model.FeedListUiEffect
import com.zion830.threedollars.ui.feed.model.FeedListUiIntent
import com.zion830.threedollars.ui.feed.model.FeedListUiState
import kotlinx.coroutines.CancellationException
import com.zion830.threedollars.ui.feed.model.FeedLocation
import com.zion830.threedollars.ui.feed.model.FeedSectionImpressionGate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import javax.inject.Inject

/**
 * 홈 바텀시트 동네 소식 탭. 목록은 서버 소식 섹션(SDUI)을 그대로 그리고,
 * 새 소식 점은 기존처럼 피드 API 최신 1건의 feedId 로 판단한다.
 */
@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val feedRepository: FeedRepository,
    private val sharedPrefUtils: SharedPrefUtils,
) : UdfViewModel<FeedListUiIntent, FeedListUiState, FeedListUiEffect>() {

    private var listJob: Job? = null
    private var latestJob: Job? = null
    private var listLocation: FeedLocation? = null
    private var failedPageCursor: String? = null
    private val cardImpressionDedup = FeedCardImpressionDedup()
    private val sectionImpression = FeedSectionImpressionGate<SDLogModel>()

    private val stateStore = MutableStateFlow(FeedListUiState(lastSeenFeedId = sharedPrefUtils.getLastSeenLocalNewsFeedId()))
    override val state: StateFlow<FeedListUiState> = stateStore.asStateFlow()

    private val _effect = Channel<FeedListUiEffect>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val effect: Flow<FeedListUiEffect> = _effect.receiveAsFlow()

    override fun dispatch(intent: FeedListUiIntent) {
        when (intent) {
            is FeedListUiIntent.OnLocationChanged -> onLocationChanged(intent.location, intent.revision)
            is FeedListUiIntent.OnTabSelectionChanged -> onTabSelectionChanged(intent.selected, intent.newsTabId)
            FeedListUiIntent.OnLoadNextPage -> loadNextPage()
            FeedListUiIntent.OnRetry -> reload()
            is FeedListUiIntent.OnCardVisible -> onCardVisible(intent.cardId)
            is FeedListUiIntent.OnContentCardClick -> onContentCardClick(intent.cardId)
            is FeedListUiIntent.OnImageClick -> onImageClick(intent)
            is FeedListUiIntent.OnAdMobClick -> SDLogSender.sendClick(stateStore.value.adMobCard(intent.cardId)?.card?.clickLog)
            is FeedListUiIntent.OnAdMobLoadFailed -> updateState { it.hideAdCard(intent.cardId) }
        }
    }

    private fun onLocationChanged(location: FeedLocation, revision: Long) {
        val current = stateStore.value
        val changed = current.changeLocation(location, revision)
        if (changed === current) return
        stateStore.value = changed
        if (changed.isTabSelected) reload() else fetchLatestFeedId()
    }

    private fun onTabSelectionChanged(selected: Boolean, newsTabId: String?) {
        val current = stateStore.value
        if (current.isTabSelected == selected && (newsTabId == null || newsTabId == current.newsTabId)) return
        updateState { it.selectTab(selected, newsTabId) }
        if (!selected) return
        val willReload = stateStore.value.needsReload
        SDLogSender.sendImpression(sectionImpression.onTabSelected(willReload))
        if (willReload) reload()
    }

    private fun reload() {
        val current = stateStore.value
        val location = current.location ?: return
        val newsTabId = current.newsTabId ?: return
        listJob?.cancel()
        failedPageCursor = null
        listLocation = location
        cardImpressionDedup.reset()
        sectionImpression.reset()
        updateState { it.startReload() }
        val revision = stateStore.value.listRevision
        fetchLatestFeedId()
        listJob = launch {
            val result = fetchPage(newsTabId, location, cursor = null)
            if (!isActive || stateStore.value.listRevision != revision) return@launch
            result
                .onSuccess { page ->
                    updateState { it.appendPage(page) }
                    SDLogSender.sendImpression(sectionImpression.onFirstPage(page.impressionLog, stateStore.value.isTabSelected))
                }
                .onFailure { updateState { it.failPage() } }
        }
    }

    private fun loadNextPage() {
        val current = stateStore.value
        val location = listLocation ?: return
        val newsTabId = current.listNewsTabId ?: return
        val cursor = current.nextCursor ?: return
        if (!current.canLoadMore || cursor == failedPageCursor) return
        val revision = current.listRevision
        updateState { it.copy(isLoading = true) }
        listJob = launch {
            val result = fetchPage(newsTabId, location, cursor)
            if (!isActive || stateStore.value.listRevision != revision) return@launch
            result
                .onSuccess { page -> updateState { it.appendPage(page) } }
                .onFailure {
                    failedPageCursor = cursor
                    updateState { it.failPage() }
                }
        }
    }

    private fun onCardVisible(cardId: String) {
        val impressionLog = stateStore.value.items.firstOrNull { it.cardId == cardId }?.impressionLog ?: return
        if (!cardImpressionDedup.shouldSend(cardId)) return
        SDLogSender.sendImpression(impressionLog)
    }

    private fun onContentCardClick(cardId: String) {
        val card = stateStore.value.contentCard(cardId) ?: return
        SDLogSender.sendClick(card.clickLog)
        card.link?.let { _effect.trySend(FeedListUiEffect.OpenLink(it)) }
    }

    private fun onImageClick(intent: FeedListUiIntent.OnImageClick) {
        val card = stateStore.value.contentCard(intent.cardId) ?: return
        FeedImageViewerRequest.of(card, intent.blockIndex, intent.imageIndex)?.let { _effect.trySend(it) }
    }

    private fun fetchLatestFeedId() {
        val current = stateStore.value
        val location = current.location ?: return
        val revision = current.locationRevision
        latestJob?.cancel()
        latestJob = launch {
            val result = feedRepository.getLocalNewsFeeds(
                cursor = null,
                mapLatitude = location.mapLatitude,
                mapLongitude = location.mapLongitude,
                maxDistanceM = location.maxDistanceM,
                deviceLatitude = location.deviceLatitude,
                deviceLongitude = location.deviceLongitude,
                size = LATEST_FEED_SIZE,
            )
            if (!isActive || stateStore.value.locationRevision != revision) return@launch
            result.onSuccess { page -> updateState { it.applyLatestFeedId(page.feeds.firstOrNull()?.feedId) } }
        }
    }

    private suspend fun fetchPage(newsTabId: String, location: FeedLocation, cursor: String?): Result<SDHomeNewsSectionModel> {
        val mapLatitude = location.mapLatitude ?: return Result.failure(IllegalStateException(MISSING_MAP_LOCATION))
        val mapLongitude = location.mapLongitude ?: return Result.failure(IllegalStateException(MISSING_MAP_LOCATION))
        return try {
            val response = screenRepository.getHomeNewsSection(
                newsTabId = newsTabId,
                mapLatitude = mapLatitude,
                mapLongitude = mapLongitude,
                maxDistanceM = location.maxDistanceM,
                cursor = cursor,
                deviceLatitude = location.deviceLatitude,
                deviceLongitude = location.deviceLongitude,
            ).first()
            val section = response.data
            if (response.ok && section != null) Result.success(section) else Result.failure(IllegalStateException(response.message.orEmpty()))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private fun updateState(transform: (FeedListUiState) -> FeedListUiState) {
        val previousSeen = stateStore.value.lastSeenFeedId
        stateStore.update(transform)
        val seen = stateStore.value.lastSeenFeedId
        if (seen != null && seen != previousSeen) sharedPrefUtils.saveLastSeenLocalNewsFeedId(seen)
    }

    private companion object {
        const val LATEST_FEED_SIZE = 1
        const val MISSING_MAP_LOCATION = "map location is required"
    }
}
