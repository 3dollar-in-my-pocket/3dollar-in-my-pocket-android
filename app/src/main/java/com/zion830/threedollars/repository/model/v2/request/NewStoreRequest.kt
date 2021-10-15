package com.zion830.threedollars.repository.model.v2.request


import com.google.gson.annotations.SerializedName

data class NewStoreRequest(
    @SerializedName("appearanceDays")
    val appearanceDays: List<String> = listOf(),
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("menus")
    val menus: List<MyMenu> = listOf(),
    @SerializedName("paymentMethods")
    val paymentMethods: List<String> = listOf(),
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("storeType")
    val storeType: String? = ""
)