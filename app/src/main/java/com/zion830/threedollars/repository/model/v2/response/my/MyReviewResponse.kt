package com.zion830.threedollars.repository.model.v2.response.my


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.repository.model.MenuType

data class MyReviewResponse(
    @SerializedName("data")
    val data: MyReviews = MyReviews(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class ReviewDetail(
    @SerializedName("categories")
    val categories: List<MenuType> = listOf(),
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("reviewId")
    val reviewId: Int = 0,
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("user")
    val user: SignUser = SignUser()
)

data class MyReviews(
    @SerializedName("contents")
    val contents: List<ReviewDetail> = listOf(),
    @SerializedName("nextCursor")
    val nextCursor: Int = 0,
    @SerializedName("totalElements")
    val totalElements: Int = 0
)