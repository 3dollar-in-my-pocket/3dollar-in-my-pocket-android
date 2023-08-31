package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("reviewId")
    val reviewId: Int = 0,
    @SerializedName("status")
    val status: String = "",
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)