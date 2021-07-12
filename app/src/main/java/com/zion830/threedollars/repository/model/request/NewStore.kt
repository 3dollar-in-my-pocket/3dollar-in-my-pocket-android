package com.zion830.threedollars.repository.model.request

import okhttp3.MultipartBody

data class NewStore(
    val appearanceDays: List<String> = listOf(),
    val categories: String = "",
    val category: String = "",
    val images: List<MultipartBody.Part> = listOf(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val menus: List<Menu> = listOf(),
    val storeName: String = "",
    val paymentMethods: List<String> = listOf(),
    val storeType: String = "",
    val userId: Int // 필수
)