package com.zion830.threedollars.ui.storeDetail.contributor.viewModel

import androidx.lifecycle.SavedStateHandle
import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.serverdriven.model.SDCardModel
import com.threedollar.common.serverdriven.model.SDCursorModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.domain.screen.repository.ScreenRepository
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
                } else {
                    stateStore.value = StoreContributorUiState.Error(response.message.orEmpty())
                }
            }
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
                    val section = response.data ?: SDSectionModel.CardsSection(type = "", cursor = SDCursorModel())
                    stateStore.value = latestState.copy(
                        screen = latestState.screen.appendCards(section),
                        isPaging = false,
                        canLoadMore = section.cursor?.hasMore == true,
                    )
                } else {
                    stateStore.value = latestState.copy(isPaging = false)
                }
            }
        }
    }

    private fun List<SDSectionModel>.firstCardsSection(): SDSectionModel.CardsSection? =
        firstOrNull { it is SDSectionModel.CardsSection } as? SDSectionModel.CardsSection

    private fun SDScreenModel.appendCards(cardsSection: SDSectionModel.CardsSection): SDScreenModel {
        var updated = false
        return copy(
            sections = sections.map { section ->
                if (section is SDSectionModel.CardsSection && !updated) {
                    updated = true
                    section.copy(
                        cards = (section.cards + cardsSection.cards).normalizedHistoryCards(),
                        cursor = cardsSection.cursor,
                    )
                } else {
                    section
                }
            }
        )
    }

    private fun List<SDCardModel>.normalizedHistoryCards(): List<SDCardModel> {
        val mergedCards = linkedMapOf<String, SDCardModel.HistoryCard>()
        val normalizedCards = mutableListOf<SDCardModel>()

        forEach { card ->
            when (card) {
                is SDCardModel.HistoryCard -> {
                    val key = card.historyMergeKey()
                    val existing = mergedCards[key]
                    if (existing == null) {
                        val normalizedCard = card.copy(
                            subTitles = card.subTitles.distinctBy { it.dedupKey() },
                        )
                        mergedCards[key] = normalizedCard
                        normalizedCards += normalizedCard
                    } else {
                        val mergedCard = existing.copy(
                            subTitles = (existing.subTitles + card.subTitles).distinctBy { it.dedupKey() },
                            subTitleChip = existing.subTitleChip ?: card.subTitleChip,
                            image = existing.image ?: card.image,
                            metadata = existing.metadata ?: card.metadata,
                            style = existing.style ?: card.style,
                        )
                        mergedCards[key] = mergedCard
                        val existingIndex = normalizedCards.indexOfFirst {
                            it is SDCardModel.HistoryCard && it.historyMergeKey() == key
                        }
                        if (existingIndex >= 0) {
                            normalizedCards[existingIndex] = mergedCard
                        }
                    }
                }

                else -> normalizedCards += card
            }
        }

        return normalizedCards
    }

    private fun SDCardModel.HistoryCard.historyMergeKey(): String {
        val titleKey = title.text.trim()
        return if (titleKey.isNotEmpty()) {
            titleKey.lowercase()
        } else {
            cardId
        }
    }

    private fun com.threedollar.common.serverdriven.model.SDTextModel.dedupKey(): String = listOf(
        text.trim(),
        isHtml.toString(),
        fontColor.orEmpty(),
    ).joinToString(separator = "|")

    private data class RequestTag(
        val version: Int,
        val isPaging: Boolean,
    )
}
