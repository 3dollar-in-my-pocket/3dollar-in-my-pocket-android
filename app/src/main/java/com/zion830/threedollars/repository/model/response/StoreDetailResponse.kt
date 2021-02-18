package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class StoreDetailResponse(
    @SerializedName("category")
    val category: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: List<Image>,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("menu")
    val menu: List<Menu>,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("review")
    val review: List<Review>,
    @SerializedName("storeName")
    val storeName: String,
    @SerializedName("user")
    val user: UserInfoResponse
)