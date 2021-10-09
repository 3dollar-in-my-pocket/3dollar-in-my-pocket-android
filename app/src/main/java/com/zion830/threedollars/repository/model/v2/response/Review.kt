package com.zion830.threedollars.repository.model.v2.response


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.repository.model.v2.response.my.User

data class Review(
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("reviewId")
    val reviewId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("user")
    val user: User = User()
)