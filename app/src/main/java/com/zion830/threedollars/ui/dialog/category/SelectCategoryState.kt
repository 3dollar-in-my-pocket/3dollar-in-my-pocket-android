package com.zion830.threedollars.ui.dialog.category


import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import kotlinx.collections.immutable.ImmutableList

sealed interface SelectCategoryState {
    data object Idle : SelectCategoryState

    data class Success(
        val bannerAd: AdvertisementModelV2?,
        val categories: ImmutableList<StoreCategory>
    ) : SelectCategoryState
}
