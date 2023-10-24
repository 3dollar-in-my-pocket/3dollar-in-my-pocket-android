package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class UserStoreRequest(
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("menus")
    val menuRequests: List<MenuRequest>,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>,
    @SerializedName("storeName")
    val storeName: String,
    @SerializedName("storeType")
    val storeType: String? = null
)