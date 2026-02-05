package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class UserStoreRequest(
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("storeName")
    val storeName: String? = null,
    @SerializedName("salesType")
    val salesType: String? = null,
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>? = null,
    @SerializedName("openingHours")
    val openingHours: OpeningHourRequest? = null,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>? = null,
    @SerializedName("menus")
    val menuRequests: List<MenuRequest>? = null
)