package com.zion830.threedollars.ui.dialog.category

import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2


sealed interface SelectCategoryEffect {
    data object InitError : SelectCategoryEffect

    data class ChangeFoodCategory(
        val item: StoreCategoryItem.Food
    ) : SelectCategoryEffect

    data class HandleAd(
        val data: AdvertisementModelV2
    ) : SelectCategoryEffect
}
