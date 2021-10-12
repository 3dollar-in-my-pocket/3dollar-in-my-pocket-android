package com.zion830.threedollars.repository.model.v2.response


import com.google.gson.annotations.SerializedName

data class NewReviewResponse(
    @SerializedName("data")
    val data: NewReview = NewReview(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class NewReview(
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("reviewId")
    val reviewId: Int = 0,
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = ""
)