package com.zion830.threedollars.repository.model.v2.response.my


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo

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
    val categories: List<String> = listOf(),
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("reviewId")
    val reviewId: Int = 0,
    @SerializedName("store")
    val store: StoreInfo = StoreInfo(),
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
    @SerializedName("user")
    val user: User = User()
)

data class MyReviews(
    @SerializedName("contents")
    val contents: List<ReviewDetail> = listOf(),
    @SerializedName("nextCursor")
    val nextCursor: Int = 0,
    @SerializedName("totalElements")
    val totalElements: Int = 0
)