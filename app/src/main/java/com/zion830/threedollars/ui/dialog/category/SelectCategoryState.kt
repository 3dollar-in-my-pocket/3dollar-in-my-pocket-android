package com.zion830.threedollars.ui.dialog.category


import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import kotlinx.collections.immutable.ImmutableList

sealed interface SelectCategoryState {
    data object Idle : SelectCategoryState

    data class Success(
        val bannerAd: AdvertisementModelV2?,
        val categories: ImmutableList<SelectableCategory>
    ) : SelectCategoryState
}

data class SelectableCategory(
    val classification: StoreCategoryClassification,
    val items: ImmutableList<SelectableCategoryItem>,
)

sealed interface SelectableCategoryItem {
    data class Default(
        val category: StoreCategoryItem
    ): SelectableCategoryItem

    data class Ad(
        val data: AdvertisementModelV2
    ): SelectableCategoryItem
}
