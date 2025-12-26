package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class UserStoreRequest(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("storeName")
    val storeName: String,
    @SerializedName("salesType")
    val salesType: String? = null,
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>,
    @SerializedName("openingHours")
    val openingHours: OpeningHourRequest? = null,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>,
    @SerializedName("menus")
    val menuRequests: List<MenuRequest>
)