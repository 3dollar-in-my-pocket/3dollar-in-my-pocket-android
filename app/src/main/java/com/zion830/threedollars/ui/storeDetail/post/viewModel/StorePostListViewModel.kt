package com.zion830.threedollars.ui.storeDetail.post.viewModel

import androidx.lifecycle.SavedStateHandle
import com.threedollar.common.base.UdfViewModel
import com.threedollar.domain.store.repository.StoreRepository
import com.zion830.threedollars.ui.storeDetail.post.model.STORE_POST_PAGE_SIZE
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListUiEffect
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListUiIntent
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListUiState
import com.zion830.threedollars.ui.storeDetail.post.model.imageSections
import com.zion830.threedollars.ui.storeDetail.post.model.likeRequestStickerId
import com.zion830.threedollars.ui.storeDetail.post.model.toggledLike
import com.zion830.threedollars.ui.storeDetail.post.ui.StorePostListActivity
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StorePostListViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    savedStateHandle: SavedStateHandle,
) : UdfViewModel<StorePostListUiIntent, StorePostListUiState, StorePostListUiEffect>() {

    private val storeId: String = savedStateHandle.get<String>(StorePostListActivity.EXTRA_STORE_ID).orEmpty()
    private var initialized = false
    private var failedPageCursor: String? = null
    private val likingPostIds = mutableSetOf<String>()

    private val stateStore = MutableStateFlow(StorePostListUiState())
    override val state: StateFlow<StorePostListUiState> = stateStore.asStateFlow()

    private val _effect = Channel<StorePostListUiEffect>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val effect: Flow<StorePostListUiEffect> = _effect.receiveAsFlow()

    override fun dispatch(intent: StorePostListUiIntent) {
        when (intent) {
            StorePostListUiIntent.OnInit -> onInit()
            StorePostListUiIntent.OnLoadNextPage -> loadPage()
            StorePostListUiIntent.OnBackClick -> _effect.trySend(StorePostListUiEffect.Close)
            is StorePostListUiIntent.OnLikeClick -> toggleLike(intent.postId)
            is StorePostListUiIntent.OnImageClick -> showImages(intent.postId, intent.imageIndex)
        }
    }

    override fun onException(exception: Throwable, tag: Any?) {
        super.onException(exception, tag)
        stateStore.update { it.copy(isLoading = false) }
        _effect.trySend(StorePostListUiEffect.ShowErrorAlert(StoreDetailErrorMessage.from(exception)))
    }

    private fun onInit() {
        if (initialized) return
        initialized = true
        loadPage()
    }

    private fun loadPage() {
        val current = stateStore.value
        if (storeId.isBlank() || !current.canLoadMore) return
        if (current.nextCursor != null && current.nextCursor == failedPageCursor) return
        stateStore.update { it.copy(isLoading = true) }

        launch {
            storeRepository.getStoreNewsPosts(storeId, current.nextCursor, STORE_POST_PAGE_SIZE)
                .onSuccess { page -> stateStore.update { it.appendPage(page).copy(isLoading = false) } }
                .onFailure {
                    failedPageCursor = current.nextCursor
                    throw it
                }
        }
    }

    private fun toggleLike(postId: String) {
        val post = stateStore.value.posts.firstOrNull { it.postId == postId } ?: return
        if (!likingPostIds.add(postId)) return

        launch {
            try {
                storeRepository.putStorePostSticker(storeId, postId, post.likeRequestStickerId())
                    .onSuccess { stateStore.update { it.replacePost(post.toggledLike()) } }
                    .onFailure { _effect.trySend(StorePostListUiEffect.ShowErrorAlert(StoreDetailErrorMessage.from(it))) }
            } finally {
                likingPostIds.remove(postId)
            }
        }
    }

    private fun showImages(postId: String, imageIndex: Int) {
        val urls = stateStore.value.posts.firstOrNull { it.postId == postId }
            ?.imageSections
            ?.map { it.url }
            ?.takeIf { it.isNotEmpty() }
            ?: return
        _effect.trySend(StorePostListUiEffect.ShowImages(urls, imageIndex.coerceIn(urls.indices)))
    }
}
