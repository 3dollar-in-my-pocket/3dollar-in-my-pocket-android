package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class StoreList(
    @SerializedName("category")
    val category: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: Int,
    @SerializedName("longitude")
    val longitude: Int,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("storeName")
    val storeName: String
)