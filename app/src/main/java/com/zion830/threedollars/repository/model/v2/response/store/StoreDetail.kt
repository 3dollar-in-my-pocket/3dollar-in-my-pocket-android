package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.repository.model.v2.response.Review
import com.zion830.threedollars.repository.model.v2.response.my.User

data class StoreDetail(
    @SerializedName("appearanceDays")
    val appearanceDays: List<String> = listOf(),
    @SerializedName("categories")
    val categories: List<String> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("distance")
    val distance: Int = 0,
    @SerializedName("images")
    val images: List<Image> = listOf(),
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0,
    @SerializedName("menus")
    val menus: List<Menu> = listOf(),
    @SerializedName("paymentMethods")
    val paymentMethods: List<String> = listOf(),
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("reviews")
    val reviews: List<Review> = listOf(),
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("storeType")
    val storeType: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
    @SerializedName("user")
    val user: User = User()
)