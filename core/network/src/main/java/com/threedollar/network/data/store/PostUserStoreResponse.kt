package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class PostUserStoreResponse(
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("categories")
    val categories: List<String> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerializedName("latitude")
    val latitude: Double? = 0.0,
    @SerializedName("longitude")
    val longitude: Double? = 0.0,
    @SerializedName("rating")
    val rating: Double? = 0.0,
    @SerializedName("salesType")
    val salesType: String? = "",
    @SerializedName("storeId")
    val storeId: Int? = 0,
    @SerializedName("storeName")
    val storeName: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
    @SerializedName("userId")
    val userId: Int? = 0
)