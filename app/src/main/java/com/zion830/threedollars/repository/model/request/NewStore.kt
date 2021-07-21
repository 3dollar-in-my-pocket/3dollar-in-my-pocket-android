package com.zion830.threedollars.repository.model.request


import com.google.gson.annotations.SerializedName

data class NewStore(
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>? = null,
    @SerializedName("latitude")
    val latitude: Int? = null,
    @SerializedName("longitude")
    val longitude: Int? = null,
    @SerializedName("menu")
    val menus: List<Menu>? = null,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>? = null,
    @SerializedName("storeName")
    val storeName: String? = null,
    @SerializedName("storeType")
    val storeType: String? = null
)