package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class StoreDetailResponse(
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>? = null,
    @SerializedName("categories")
    val categories: List<String>? = null,
    @SerializedName("category")
    val category: String = "",
    @SerializedName("distance")
    val distance: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("image")
    val image: List<Image>? = null,
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("menu")
    val menu: List<Menu>? = null,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>? = null,
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("review")
    val review: List<Review>? = null,
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("storeType")
    val storeType: String = "",
    @SerializedName("user")
    val user: User? = null
)