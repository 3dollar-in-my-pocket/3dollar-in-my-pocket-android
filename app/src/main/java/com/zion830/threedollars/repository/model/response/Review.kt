package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class Review(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("user")
    val user: User = User()
)