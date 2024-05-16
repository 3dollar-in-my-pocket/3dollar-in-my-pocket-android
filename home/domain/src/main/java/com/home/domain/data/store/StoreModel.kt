package com.home.domain.data.store

import com.home.domain.request.FilterConditionsTypeModel

data class StoreModel(
    val accountModel: AccountModel = AccountModel(),
    val addressModel: AddressModel = AddressModel(),
    val categories: List<CategoryModel> = listOf(),
    val createdAt: String = "",
    val isDeleted: Boolean = false,
    val locationModel: LocationModel = LocationModel(),
    val activitiesStatus: FilterConditionsTypeModel = FilterConditionsTypeModel.NO_RECENT_ACTIVITY,
    val storeId: String = "",
    val storeName: String = "",
    val storeType: String = "",
    val updatedAt: String = "",
)