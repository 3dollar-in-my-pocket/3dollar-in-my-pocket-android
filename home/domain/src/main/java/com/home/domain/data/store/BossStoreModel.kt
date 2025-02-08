package com.home.domain.data.store

interface BossStoreDetailItem

data class BossStoreModel(
    val address: AddressModel = AddressModel(),
    val appearanceDayModels: List<AppearanceDayModel> = listOf(),
    val categories: List<CategoryModel> = listOf(),
    val createdAt: String = "",
    val imageUrl: String? = "",
    val introduction: String? = "",
    val location: LocationModel? = LocationModel(),
    val menuModels: List<MenuModel> = listOf(),
    val name: String = "",
    val snsUrl: String? = "",
    val contactsNumber: String? = "",
    val storeId: String = "",
    val updatedAt: String = "",
    val isOwner: Boolean = false,
    val accountNumbers: List<AccountNumberModel>? = listOf(),
)
