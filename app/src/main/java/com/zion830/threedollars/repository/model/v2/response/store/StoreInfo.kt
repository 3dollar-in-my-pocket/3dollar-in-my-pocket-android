package com.zion830.threedollars.repository.model.v2.response.store

import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.repository.model.MenuType

data class StoreInfo(
    @SerializedName("categories")
    val categories: List<MenuType> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("distance")
    val distance: Int = 0,
    @SerializedName("latitude")
    val latitude: Int = 0,
    @SerializedName("longitude")
    val longitude: Int = 0,
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = ""
)