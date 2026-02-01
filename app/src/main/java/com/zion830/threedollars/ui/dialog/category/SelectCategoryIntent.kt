package com.zion830.threedollars.ui.dialog.category

import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2

sealed interface SelectCategoryIntent {
    data object OnInit : SelectCategoryIntent

    data class OnCategoryClick(
        val item: StoreCategoryItem
    ) : SelectCategoryIntent

    data class OnCategoryAdBannerClick(
        val data: AdvertisementModelV2
    ) : SelectCategoryIntent
}
