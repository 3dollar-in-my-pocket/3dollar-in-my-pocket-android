package com.home.domain.data.store

data class MenuModel(
    val imageUrl: String? = "",
    val name: String = "",
    val price: Int = 0
) : BossStoreDetailItem