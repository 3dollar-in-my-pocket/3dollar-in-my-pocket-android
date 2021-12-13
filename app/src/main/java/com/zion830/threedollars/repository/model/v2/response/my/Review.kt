package com.zion830.threedollars.repository.model.v2.response.my


import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("reviewId")
    val reviewId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("user")
    val user: User = User()
)