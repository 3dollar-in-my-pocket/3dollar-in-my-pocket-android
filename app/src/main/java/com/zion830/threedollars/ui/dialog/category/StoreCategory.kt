package com.zion830.threedollars.ui.dialog.category

import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import kotlinx.collections.immutable.ImmutableList

data class StoreCategory(
    val classification: StoreCategoryClassification,
    val items: ImmutableList<StoreCategoryItem>,
)

data class StoreCategoryClassification(
    val type: String,
    val name: String,
    val priority: Int
)

sealed interface StoreCategoryItem {
    data class Ad(
        val data: AdvertisementModelV2
    ) : StoreCategoryItem

    data class Food(
        val id: String,
        val name: String,
        val description: String,
        val imageUrl: String,
        val disableImageUrl: String,
        val isNew: Boolean
    ) : StoreCategoryItem
}
