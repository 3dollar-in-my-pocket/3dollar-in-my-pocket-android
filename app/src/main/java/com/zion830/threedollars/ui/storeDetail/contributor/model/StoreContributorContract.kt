package com.zion830.threedollars.ui.storeDetail.contributor.model

import androidx.compose.runtime.Immutable
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDScreenModel

@Immutable
sealed interface StoreContributorUiState {
    data object Loading : StoreContributorUiState

    data class Success(
        val screen: SDScreenModel,
        val isPaging: Boolean = false,
        val canLoadMore: Boolean = false,
    ) : StoreContributorUiState

    data class Error(
        val message: String = "",
    ) : StoreContributorUiState
}

@Immutable
sealed interface StoreContributorUiIntent {
    data object OnInit : StoreContributorUiIntent
    data object OnRefresh : StoreContributorUiIntent
    data object OnCloseClick : StoreContributorUiIntent
    data object OnLoadNextPage : StoreContributorUiIntent
    data class OnActionClick(val action: SDLinkModel) : StoreContributorUiIntent
}

@Immutable
sealed interface StoreContributorUiEffect {
    data object Close : StoreContributorUiEffect
    data class ExecuteAction(val action: SDLinkModel) : StoreContributorUiEffect
}
