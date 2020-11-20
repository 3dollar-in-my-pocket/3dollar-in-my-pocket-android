package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class MyReviewResponse(
    @SerializedName("content")
    val review: List<Review>? = emptyList(),
    @SerializedName("totalElements")
    val totalElements: Int = 0,
    @SerializedName("totalPages")
    val totalPages: Int = 0
)