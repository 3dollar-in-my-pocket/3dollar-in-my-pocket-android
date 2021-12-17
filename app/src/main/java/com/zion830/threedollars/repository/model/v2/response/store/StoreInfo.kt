package com.zion830.threedollars.repository.model.v2.response.store

import com.google.gson.annotations.SerializedName

data class StoreInfo(
    @SerializedName("categories")
    val categories: List<String> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("distance")
    val distance: Int = 0,
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("visitHistory")
    val visitHistory: VisitHistory = VisitHistory(),
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false
)