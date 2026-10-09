package com.zion830.threedollars.ui.storeDetail.contributor.viewModel

import androidx.lifecycle.SavedStateHandle
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.SDClickLogger
import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.domain.screen.repository.ScreenRepository
import com.zion830.threedollars.ui.storeDetail.contributor.model.appendFirstCardsSection
import com.zion830.threedollars.ui.storeDetail.contributor.model.createStoreContributorEditClickEvent
import com.zion830.threedollars.ui.storeDetail.contributor.model.storeContributorPageViewClassName
import com.zion830.threedollars.ui.storeDetail.contributor.model.storeContributorScreenName
import com.zion830.threedollars.ui.storeDetail.contributor.model.firstCardsSection
import com.zion830.threedollars.ui.storeDetail.contributor.model.StoreContributorUiEffect
import com.zion830.threedollars.ui.storeDetail.contributor.model.StoreContributorUiIntent
import com.zion830.threedollars.ui.storeDetail.contributor.model.StoreContributorUiState
import com.zion830.threedollars.ui.storeDetail.contributor.ui.StoreContributorActivity
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
class StoreContributorViewModel @Inject constructor(
    private val screenRepository: ScreenRepository,
    savedStateHandle: SavedStateHandle,
) : UdfViewModel<StoreContributorUiIntent, StoreContributorUiState, StoreContributorUiEffect>() {

    private val storeId: String = savedStateHandle.get<String>(StoreContributorActivity.EXTRA_STORE_ID).orEmpty()
    private var initialized = false
    private var loadVersion = 0
    private var isPageViewPending = false

    private val stateStore = MutableStateFlow<StoreContributorUiState>(StoreContributorUiState.Loading)
    override val state: StateFlow<StoreContributorUiState> = stateStore.asStateFlow()

    private val _effect = Channel<StoreContributorUiEffect>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val effect: Flow<StoreContributorUiEffect> = _effect.receiveAsFlow()

    override fun dispatch(intent: StoreContributorUiIntent) {
        when (intent) {
            StoreContributorUiIntent.OnInit -> onInit()
            StoreContributorUiIntent.OnRefresh -> refresh()
            StoreContributorUiIntent.OnCloseClick -> _effect.trySend(StoreContributorUiEffect.Close)
            StoreContributorUiIntent.OnLoadNextPage -> loadNextPage()
            StoreContributorUiIntent.OnShown -> {
                isPageViewPending = true
                sendPendingPageView()
            }
            StoreContributorUiIntent.OnEditClick -> sendEditClickLog()
            is StoreContributorUiIntent.OnActionClick -> _effect.trySend(StoreContributorUiEffect.ExecuteAction(intent.action))
        }
    }

    override fun onException(exception: Throwable, tag: Any?) {
        if (tag is RequestTag && tag.version != loadVersion) return

        super.onException(exception, tag)
        val currentState = stateStore.value
        stateStore.value = when (currentState) {
            is StoreContributorUiState.Success -> currentState.copy(isPaging = false)
            else -> StoreContributorUiState.Error(exception.message.orEmpty())
        }
        sendPendingPageView()
    }

    private fun onInit() {
        if (storeId.isBlank()) {
            stateStore.value = StoreContributorUiState.Error("정보를 불러오지 못했어요")
            return
        }
        if (initialized) return
        initialized = true
        loadScreen()
    }

    private fun refresh() {
        if (storeId.isBlank()) {
            stateStore.value = StoreContributorUiState.Error("정보를 불러오지 못했어요")
            return
        }
        loadScreen()
    }

    private fun loadScreen() {
        val version = ++loadVersion
        stateStore.value = StoreContributorUiState.Loading

        launch(tag = RequestTag(version = version, isPaging = false)) {
            screenRepository.getStoreContributorScreen(storeId).collect { response ->
                if (version != loadVersion) return@collect
                if (response.ok) {
                    val screen = response.data ?: SDScreenModel()
                    val cardsSection = screen.sections.firstCardsSection()
                    stateStore.value = StoreContributorUiState.Success(
                        screen = screen,
                        canLoadMore = cardsSection?.cursor?.hasMore == true,
                    )
                    sendPendingPageView()
                } else {
                    stateStore.value = StoreContributorUiState.Error(response.message.orEmpty())
                    sendPendingPageView()
                }
            }
        }
    }

    /**
     * page_view 는 서버 viewLog(store_id 포함)로 보낸다. 화면을 아직 못 받았으면 받은 뒤에 보내고,
     * 응답에 viewLog 가 없거나 실패하면 store_id 를 붙인 정적 page_view 로 대신한다.
     */
    private fun sendPendingPageView() {
        if (!isPageViewPending) return
        val currentState = stateStore.value
        if (currentState is StoreContributorUiState.Loading) return
        isPageViewPending = false
        val viewLog = (currentState as? StoreContributorUiState.Success)?.screen?.viewLog
        if (viewLog != null) {
            SDClickLogger.send(viewLog)
        } else {
            LogManager.sendPageView(
                storeContributorScreenName,
                storeContributorPageViewClassName,
                mapOf(ParameterName.STORE_ID to storeId),
            )
        }
    }

    private fun sendEditClickLog() {
        val clickLog = (stateStore.value as? StoreContributorUiState.Success)
            ?.screen
            ?.sections
            ?.filterIsInstance<SDSectionModel.ActionBarSection>()
            ?.firstNotNullOfOrNull { it.actionBar.clickLog }
        if (clickLog != null) {
            SDClickLogger.send(clickLog)
        } else {
            LogManager.sendEvent(createStoreContributorEditClickEvent(storeId))
        }
    }

    private fun loadNextPage() {
        val currentState = stateStore.value as? StoreContributorUiState.Success ?: return
        if (currentState.isPaging || !currentState.canLoadMore) return

        val nextCursor = currentState.screen.sections.firstCardsSection()?.cursor?.nextCursor ?: return
        val version = loadVersion
        stateStore.update { currentState.copy(isPaging = true) }

        launch(tag = RequestTag(version = version, isPaging = true)) {
            screenRepository.getStoreContributorHistories(storeId, nextCursor).collect { response ->
                if (version != loadVersion) return@collect
                val latestState = stateStore.value as? StoreContributorUiState.Success ?: return@collect
                if (response.ok) {
                    val section = response.data ?: SDSectionModel.CardsSection(type = "")
                    stateStore.value = latestState.copy(
                        screen = latestState.screen.appendFirstCardsSection(section),
                        isPaging = false,
                        canLoadMore = section.cursor?.hasMore == true,
                    )
                } else {
                    stateStore.value = latestState.copy(isPaging = false)
                }
            }
        }
    }

    private data class RequestTag(
        val version: Int,
        val isPaging: Boolean,
    )
}
