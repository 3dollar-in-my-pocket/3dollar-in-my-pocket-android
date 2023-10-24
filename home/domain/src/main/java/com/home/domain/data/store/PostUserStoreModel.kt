package com.home.domain.data.store

data class PostUserStoreModel(
    val address: AddressModel = AddressModel(),
    val categories: List<String> = listOf(),
    val createdAt: String = "",
    val isDeleted: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val rating: Double = 0.0,
    val salesType: SalesType = SalesType.NONE,
    val storeId: Int = 0,
    val storeName: String = "",
    val updatedAt: String = "",
    val userId: Int = 0
)