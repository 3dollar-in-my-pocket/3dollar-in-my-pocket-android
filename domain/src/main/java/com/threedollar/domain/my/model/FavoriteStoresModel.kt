package com.threedollar.domain.my.model

data class FavoriteStoresModel(
    val contents: List<FavoriteStoreModel> = emptyList(),
    val cursor: CursorModel? = null
)

data class FavoriteStoreModel(
    val storeId: String,
    val storeName: String,
    val storeType: String,
    val categories: List<CategoryModel>,
    val rating: Double,
    val reviewsCount: Int,
    val isDeleted: Boolean,
    val address: AddressModel,
    val distance: Int
)

data class CategoryModel(
    val categoryId: String,
    val name: String,
    val imageUrl: String
)

data class AddressModel(
    val fullAddress: String,
    val district: String
)

data class CursorModel(
    val hasMore: Boolean,
    val nextCursor: String?
)