package com.home.domain.data.store


data class UserStoreModel(
    val address: AddressModel = AddressModel(),
    val appearanceDays: List<String> = listOf(),
    val categories: List<CategoryModel> = listOf(),
    val createdAt: String = "",
    val location: LocationModel = LocationModel(),
    val menus: List<UserStoreMenuModel> = listOf(),
    val name: String = "",
    val paymentMethods: List<String> = listOf(),
    val rating: Int = 0,
    val salesType: String? = null,
    val storeId: Int = 0,
    val updatedAt: String = ""
)