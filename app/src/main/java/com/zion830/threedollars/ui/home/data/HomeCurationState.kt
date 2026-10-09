package com.zion830.threedollars.ui.home.data

import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel

data class HomeCurationUiState(
    val tabs: List<SDHomeBottomSheetTabModel> = emptyList(),
    val selectedTabId: String? = null,
    val section: SDHomeCurationSectionModel? = null,
    val carousels: Map<String, HomeCurationCarouselState> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sectionRequestId: Long = 0L,
    val sectionTabId: String? = null,
    val requestLocation: HomeCurationRequestLocation? = null,
)

data class HomeCurationCarouselState(
    val selectedCategoryId: String,
    val cards: SDHomeCurationCardsModel,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val requestId: Long = 0L,
    val pendingCategoryId: String? = null,
)

data class HomeCurationRequestLocation(
    val mapLatitude: Double,
    val mapLongitude: Double,
    val deviceLatitude: Double?,
    val deviceLongitude: Double?,
)

object HomeCurationStateReducer {
    fun updateTabs(state: HomeCurationUiState, tabs: SDHomeBottomSheetTabsModel): HomeCurationUiState {
        val supportedTabs = tabs.tabs.filter { it.viewType == "CURATION" || it.viewType == "STORE_LIST" }
        val selectedTabId = state.selectedTabId?.takeIf { current -> supportedTabs.any { it.tabId == current } }
            ?: supportedTabs.firstOrNull { it.defaultSelected }?.tabId
            ?: supportedTabs.firstOrNull()?.tabId
        return state.copy(tabs = supportedTabs, selectedTabId = selectedTabId)
    }

    fun selectTab(state: HomeCurationUiState, tabId: String): HomeCurationUiState {
        if (state.selectedTabId == tabId || state.tabs.none { it.tabId == tabId }) return state
        return state.copy(selectedTabId = tabId)
    }

    fun startSectionLoad(
        state: HomeCurationUiState,
        tabId: String,
        location: HomeCurationRequestLocation,
        requestId: Long,
    ): HomeCurationUiState = state.copy(
        isLoading = true,
        errorMessage = null,
        sectionRequestId = requestId,
        sectionTabId = tabId,
        requestLocation = location,
        carousels = state.carousels.mapValues { (_, carousel) ->
            carousel.copy(isLoading = false, errorMessage = null, pendingCategoryId = null)
        },
    )

    fun applySection(state: HomeCurationUiState, section: SDHomeCurationSectionModel, requestId: Long): HomeCurationUiState {
        if (state.sectionRequestId != requestId || !state.isLoading) return state
        return state.copy(
            section = section,
            carousels = section.items.filterIsInstance<SDHomeCurationItemModel.Carousel>().associate { carousel ->
                carousel.carouselId to HomeCurationCarouselState(
                    selectedCategoryId = carousel.defaultCategoryId,
                    cards = SDHomeCurationCardsModel(cards = carousel.cards),
                )
            },
            isLoading = false,
            errorMessage = null,
        )
    }

    fun failSection(state: HomeCurationUiState, message: String, requestId: Long): HomeCurationUiState {
        if (state.sectionRequestId != requestId || !state.isLoading) return state
        return state.copy(isLoading = false, errorMessage = message)
    }

    fun startCategoryLoad(
        state: HomeCurationUiState,
        carouselId: String,
        categoryId: String,
        requestId: Long,
    ): HomeCurationUiState {
        val carousel = state.carousels[carouselId] ?: return state
        return state.copy(
            carousels = state.carousels + (carouselId to carousel.copy(
                isLoading = true,
                errorMessage = null,
                requestId = requestId,
                pendingCategoryId = categoryId,
            )),
        )
    }

    fun applyCategory(
        state: HomeCurationUiState,
        carouselId: String,
        requestId: Long,
        sectionRequestId: Long,
        cards: SDHomeCurationCardsModel,
    ): HomeCurationUiState {
        val carousel = state.carousels[carouselId] ?: return state
        if (state.sectionRequestId != sectionRequestId || carousel.requestId != requestId || !carousel.isLoading) return state
        val categoryId = carousel.pendingCategoryId ?: return state
        return state.copy(
            carousels = state.carousels + (carouselId to carousel.copy(
                selectedCategoryId = categoryId,
                cards = cards,
                isLoading = false,
                errorMessage = null,
                pendingCategoryId = null,
            )),
        )
    }

    fun failCategory(
        state: HomeCurationUiState,
        carouselId: String,
        requestId: Long,
        sectionRequestId: Long,
        message: String,
    ): HomeCurationUiState {
        val carousel = state.carousels[carouselId] ?: return state
        if (state.sectionRequestId != sectionRequestId || carousel.requestId != requestId || !carousel.isLoading) return state
        return state.copy(
            carousels = state.carousels + (carouselId to carousel.copy(
                isLoading = false,
                errorMessage = message,
            )),
        )
    }
}
