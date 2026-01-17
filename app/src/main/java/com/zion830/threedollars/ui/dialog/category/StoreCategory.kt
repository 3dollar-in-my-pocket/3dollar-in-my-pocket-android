package com.zion830.threedollars.ui.dialog.category

data class StoreCategory(
    val classification: StoreCategoryClassification,
    val items: List<StoreCategoryItem>,
)

data class StoreCategoryClassification(
    val type: String,
    val name: String,
    val priority: Int
)

data class StoreCategoryItem(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val disableImageUrl: String,
    val isNew: Boolean
)
