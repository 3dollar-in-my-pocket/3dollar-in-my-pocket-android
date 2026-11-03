package com.zion830.threedollars.ui.feed.viewModel

import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.domain.community.repository.CommunityRepository
import com.threedollar.domain.feed.repository.FeedRepository
import com.zion830.threedollars.ui.feed.model.FeedListUiEffect
import com.zion830.threedollars.ui.feed.model.FeedListUiIntent
import com.zion830.threedollars.ui.feed.model.FeedListUiState
import com.zion830.threedollars.ui.feed.model.FeedLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val communityRepository: CommunityRepository,
    private val sharedPrefUtils: SharedPrefUtils,
) : UdfViewModel<FeedListUiIntent, FeedListUiState, FeedListUiEffect>() {

    private var listJob: Job? = null
    private var latestJob: Job? = null
    private var listLocation: FeedLocation? = null
    private var failedPageCursor: String? = null
    private var hasRequestedAdvertisement = false

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
            is FeedListUiIntent.OnTabSelectionChanged -> onTabSelectionChanged(intent.selected)
            FeedListUiIntent.OnLoadNextPage -> loadNextPage()
            FeedListUiIntent.OnRetry -> reload()
            is FeedListUiIntent.OnFeedClick -> intent.feed.link?.let { _effect.trySend(FeedListUiEffect.OpenLink(it)) }
            is FeedListUiIntent.OnAdvertisementClick -> _effect.trySend(FeedListUiEffect.OpenAdvertisement(intent.advertisement))
        }
    }

    private fun onLocationChanged(location: FeedLocation, revision: Long) {
        val current = stateStore.value
        val changed = current.changeLocation(location, revision)
        if (changed === current) return
        stateStore.value = changed
        if (changed.isTabSelected) reload() else fetchLatestFeedId()
    }

    private fun onTabSelectionChanged(selected: Boolean) {
        if (stateStore.value.isTabSelected == selected) return
        updateState { it.selectTab(selected) }
        if (selected && stateStore.value.needsReload) reload()
    }

    private fun reload() {
        val location = stateStore.value.location ?: return
        listJob?.cancel()
        failedPageCursor = null
        listLocation = location
        updateState { it.startReload() }
        val revision = stateStore.value.listRevision
        fetchAdvertisement(location)
        listJob = launch {
            val result = fetchPage(location, cursor = null)
            if (!isActive || stateStore.value.listRevision != revision) return@launch
            result
                .onSuccess { page -> updateState { it.appendPage(page) } }
                .onFailure { updateState { it.failPage() } }
        }
    }

    private fun loadNextPage() {
        val current = stateStore.value
        val location = listLocation ?: return
        val cursor = current.nextCursor ?: return
        if (!current.canLoadMore || cursor == failedPageCursor) return
        val revision = current.listRevision
        updateState { it.copy(isLoading = true) }
        listJob = launch {
            val result = fetchPage(location, cursor)
            if (!isActive || stateStore.value.listRevision != revision) return@launch
            result
                .onSuccess { page -> updateState { it.appendPage(page) } }
                .onFailure {
                    failedPageCursor = cursor
                    updateState { it.failPage() }
                }
        }
    }

    private fun fetchLatestFeedId() {
        val current = stateStore.value
        val location = current.location ?: return
        val revision = current.locationRevision
        latestJob?.cancel()
        latestJob = launch {
            val result = fetchPage(location, cursor = null, size = LATEST_FEED_SIZE)
            if (!isActive || stateStore.value.locationRevision != revision) return@launch
            result.onSuccess { page -> updateState { it.applyLatestFeedId(page.feeds.firstOrNull()?.feedId) } }
        }
    }

    private suspend fun fetchPage(
        location: FeedLocation,
        cursor: String?,
        size: Int = FeedRepository.DEFAULT_PAGE_SIZE,
    ) = feedRepository.getLocalNewsFeeds(
        cursor = cursor,
        mapLatitude = location.mapLatitude,
        mapLongitude = location.mapLongitude,
        deviceLatitude = location.deviceLatitude,
        deviceLongitude = location.deviceLongitude,
        size = size,
    )

    /** 서버 광고가 없거나 실패하면 AdMob 배너로 대신하므로 에러를 알리지 않는다. */
    private fun fetchAdvertisement(location: FeedLocation) {
        if (hasRequestedAdvertisement) return
        val latitude = location.deviceLatitude ?: return
        val longitude = location.deviceLongitude ?: return
        hasRequestedAdvertisement = true
        launch {
            val response = communityRepository.getAdvertisements(
                position = AdvertisementsPosition.LOCAL_NEWS_FEED,
                deviceLatitude = latitude,
                deviceLongitude = longitude,
            ).firstOrNull()
            if (response == null || !response.ok) {
                hasRequestedAdvertisement = false
                return@launch
            }
            val advertisement = response.data?.firstOrNull()
            stateStore.update { it.copy(advertisement = advertisement) }
        }.invokeOnCompletion { cause -> if (cause != null) hasRequestedAdvertisement = false }
    }

    private fun updateState(transform: (FeedListUiState) -> FeedListUiState) {
        val previousSeen = stateStore.value.lastSeenFeedId
        stateStore.update(transform)
        val seen = stateStore.value.lastSeenFeedId
        if (seen != null && seen != previousSeen) sharedPrefUtils.saveLastSeenLocalNewsFeedId(seen)
    }

    private companion object {
        const val LATEST_FEED_SIZE = 1
    }
}
