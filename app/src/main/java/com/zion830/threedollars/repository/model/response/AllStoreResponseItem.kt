package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class AllStoreResponseItem(
    @SerializedName("category")
    val category: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("storeName")
    val storeName: String
)