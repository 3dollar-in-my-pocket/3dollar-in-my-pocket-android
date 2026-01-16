package com.zion830.threedollars.ui.dialog.category

import kotlinx.collections.immutable.ImmutableList

sealed interface SelectCategoryState {
    data object Idle : SelectCategoryState

    data class Success(
        val categories: ImmutableList<StoreCategory>
    ) : SelectCategoryState
}
