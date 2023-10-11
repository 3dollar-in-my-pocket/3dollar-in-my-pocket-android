package com.home.domain.data.store

import java.io.Serializable


data class UserStoreModel(
    val address: AddressModel = AddressModel(),
    val appearanceDays: List<DayOfTheWeekType> = listOf(),
    val categories: List<CategoryModel> = listOf(),
    val createdAt: String = "",
    val location: LocationModel = LocationModel(),
    val menus: List<UserStoreMenuModel> = listOf(),
    val name: String = "",
    val paymentMethods: List<PaymentType> = listOf(),
    val rating: Int = 0,
    val salesType: SalesType = SalesType.NONE,
    val storeId: Int = 0,
    val updatedAt: String = "",
) : Serializable