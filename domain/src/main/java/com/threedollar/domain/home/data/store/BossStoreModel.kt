package com.threedollar.domain.home.data.store

interface BossStoreDetailItem

data class BossStoreModel(
    val storeId: String = "",
    val isOwner: Boolean = false,
    val name: String = "",
    val rating: Float = 0f,
    val location: LocationModel = LocationModel(),
    val address: AddressModel = AddressModel(),
    val representativeImages: List<ImageModel> = emptyList(),
    val introduction: String = "",
    val snsUrl: String = "",
    val menus: List<MenuModel> = emptyList(),
    val appearanceDays: List<AppearanceDayModel> = emptyList(),
    val categories: List<CategoryModel> = emptyList(),
    val accountNumbers: List<AccountNumberModel> = emptyList(),
    val contactsNumbers: List<ContactNumberModel> = emptyList(),
    val activitiesStatus: ActivitiesStatus = ActivitiesStatus.OTHER,
    val createdAt: String = "",
    val updatedAt: String = ""
)
