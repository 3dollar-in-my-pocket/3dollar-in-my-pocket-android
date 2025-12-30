package com.threedollar.domain.home.data.store

data class PostUserStoreModel(
    val storeId: Int = 0,
    val isOwner: Boolean = false,
    val name: String = "",
    val salesType: SalesType = SalesType.NONE,
    val salesTypeDescription: String = "",
    val rating: Double = 0.0,
    val location: LocationModel = LocationModel(),
    val address: AddressModel = AddressModel(),
    val categories: List<CategoryModel> = listOf(),
    val appearanceDays: List<String> = listOf(),
    val openingHours: OpeningHoursModel? = null,
    val paymentMethods: List<String> = listOf(),
    val menus: List<MenuV3Model> = listOf(),
    val isDeleted: Boolean = false,
    val activitiesStatus: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class MenuV3Model(
    val name: String = "",
    val price: Int = 0,
    val count: Int = 0,
    val description: String = "",
    val category: CategoryModel = CategoryModel()
)