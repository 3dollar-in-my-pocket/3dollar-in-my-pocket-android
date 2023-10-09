package com.home.domain.data.store

interface UserStoreDetailItem

data class UserStoreMenuModel(
    val category: CategoryModel = CategoryModel(),
    val menuId: Int = 0,
    val name: String? = "-",
    val price: String? = "-"
) : UserStoreDetailItem