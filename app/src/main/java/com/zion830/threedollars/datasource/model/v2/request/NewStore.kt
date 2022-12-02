package com.zion830.threedollars.datasource.model.v2.request


import com.google.gson.annotations.SerializedName

data class NewStore(
    @SerializedName("categories")
    val categories: List<String>,
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("menu")
    val menus: List<Menu>,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>,
    @SerializedName("storeName")
    val storeName: String,
    @SerializedName("storeType")
    val storeType: String
)