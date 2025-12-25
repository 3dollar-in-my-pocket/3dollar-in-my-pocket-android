package com.threedollar.domain.my.model

data class VisitHistoryModel(
    val contents: List<VisitHistoryItemModel> = emptyList(),
    val cursor: CursorModel? = null
)

data class VisitHistoryItemModel(
    val visitHistoryId: String,
    val store: StoreModel,
    val dateOfVisit: String,
    val type: String
)

data class StoreModel(
    val storeId: String,
    val storeName: String,
    val storeType: String,
    val rating: Double,
    val reviewsCount: Int,
    val categories: List<CategoryModel>,
    val address: AddressModel,
    val distance: Int,
    val isDeleted: Boolean
)