package com.zion830.threedollars.ui.feed.viewModel

import androidx.lifecycle.SavedStateHandle
import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.coroutines.CoroutineTagElement
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.domain.community.repository.CommunityRepository
import com.threedollar.domain.feed.repository.FeedRepository
import com.threedollar.network.result.ApiException
import com.zion830.threedollars.ui.feed.model.FeedListUiEffect
import com.zion830.threedollars.ui.feed.model.FeedListUiIntent
import com.zion830.threedollars.ui.feed.model.FeedListUiState
import com.zion830.threedollars.ui.feed.model.FeedLocation
import com.zion830.threedollars.ui.feed.ui.FeedListActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val communityRepository: CommunityRepository,
    savedStateHandle: SavedStateHandle,
) : UdfViewModel<FeedListUiIntent, FeedListUiState, FeedListUiEffect>() {

    private val location: FeedLocation = FeedListActivity.readLocation(savedStateHandle)
    private var initialized = false
    private var failedPageCursor: String? = null

    private val stateStore = MutableStateFlow(FeedListUiState())
    override val state: StateFlow<FeedListUiState> = stateStore.asStateFlow()

    private val _effect = Channel<FeedListUiEffect>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val effect: Flow<FeedListUiEffect> = _effect.receiveAsFlow()

    override fun dispatch(intent: FeedListUiIntent) {
        when (intent) {
            FeedListUiIntent.OnInit -> onInit()
            FeedListUiIntent.OnRefresh -> refresh()
            FeedListUiIntent.OnLoadNextPage -> loadNextPage()
            FeedListUiIntent.OnCloseClick -> _effect.trySend(FeedListUiEffect.Close)
            is FeedListUiIntent.OnFeedClick -> intent.feed.link?.let { _effect.trySend(FeedListUiEffect.OpenLink(it)) }
            is FeedListUiIntent.OnAdvertisementClick -> _effect.trySend(FeedListUiEffect.OpenAdvertisement(intent.advertisement))
        }
    }

    override fun onException(exception: Throwable, tag: Any?) {
        super.onException(exception, tag)
        if ((tag as? CoroutineTagElement)?.tag == AD_TAG) return
        stateStore.update { it.copy(isLoading = false, isRefreshing = false) }
        _effect.trySend(FeedListUiEffect.ShowErrorAlert((exception as? ApiException)?.message))
    }

    private fun onInit() {
        if (initialized) return
        initialized = true
        fetchAdvertisement()
        stateStore.update { it.copy(isLoading = true) }
        launch {
            val page = fetchPage(cursor = null).getOrThrow()
            stateStore.update { it.appendPage(page) }
        }
    }

    private fun refresh() {
        if (stateStore.value.isRefreshing) return
        failedPageCursor = null
        stateStore.update { it.copy(isRefreshing = true) }
        launch {
            val page = fetchPage(cursor = null).getOrThrow()
            stateStore.update { it.replacePage(page) }
        }
    }

    private fun loadNextPage() {
        val current = stateStore.value
        val cursor = current.nextCursor ?: return
        if (!current.canLoadMore || cursor == failedPageCursor) return
        stateStore.update { it.copy(isLoading = true) }
        launch {
            val page = fetchPage(cursor).onFailure { failedPageCursor = cursor }.getOrThrow()
            stateStore.update { it.appendPage(page) }
        }
    }

    private suspend fun fetchPage(cursor: String?) = feedRepository.getLocalNewsFeeds(
        cursor = cursor,
        mapLatitude = location.mapLatitude,
        mapLongitude = location.mapLongitude,
        deviceLatitude = location.deviceLatitude,
        deviceLongitude = location.deviceLongitude,
    )

    /** 서버 광고가 없거나 실패하면 AdMob 배너로 대신하므로 에러를 알리지 않는다. */
    private fun fetchAdvertisement() {
        val latitude = location.deviceLatitude ?: return
        val longitude = location.deviceLongitude ?: return
        launch(tag = AD_TAG) {
            val advertisement = communityRepository.getAdvertisements(
                position = AdvertisementsPosition.LOCAL_NEWS_FEED,
                deviceLatitude = latitude,
                deviceLongitude = longitude,
            ).firstOrNull()?.takeIf { it.ok }?.data?.firstOrNull()
            stateStore.update { it.copy(advertisement = advertisement) }
        }
    }

    private companion object {
        const val AD_TAG = "advertisement"
    }
}
