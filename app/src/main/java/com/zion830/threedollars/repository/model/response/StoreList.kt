package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class StoreList(
    @SerializedName("category")
    val category: String = "BUNGEOPPANG",
    @SerializedName("distance")
    val distance: Int = 50,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("rating")
    val rating: Float = 1f,
    @SerializedName("storeName")
    val storeName: String = ""
)