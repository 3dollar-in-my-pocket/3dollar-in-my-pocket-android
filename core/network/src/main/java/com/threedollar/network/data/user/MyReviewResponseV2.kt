package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName
import com.threedollar.network.data.store.Account
import com.threedollar.network.data.store.Address
import com.threedollar.network.data.store.Category
import com.threedollar.network.data.store.Cursor
import com.threedollar.network.data.store.Location

data class MyReviewResponseV2(
    @SerializedName("contents")
    val contents: List<MyReviewResponseData>,
    @SerializedName("cursor")
    val cursor: Cursor
)

data class MyReviewResponseData(
    @SerializedName("review")
    val review: ReviewContent = ReviewContent(),
    @SerializedName("store")
    val store: Store = Store(),
    @SerializedName("reviewWriter")
    val reviewWriter: ReviewWriter = ReviewWriter()
)

data class ReviewContent(
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
    @SerializedName("reviewId")
    val reviewId: Int? = 0,
    @SerializedName("storeId")
    val storeId: Int? = 0,
    @SerializedName("userId")
    val userId: Int? = 0,
    @SerializedName("rating")
    val rating: Int? = 0,
    @SerializedName("contents")
    val contents: String? = "",
    @SerializedName("status")
    val status: String? = "POSTED",
    @SerializedName("isOwner")
    val isOwner: Boolean? = false
)

data class Store(
    @SerializedName("storeType")
    val storeType: String? = "USER_STORE",
    @SerializedName("storeId")
    val storeId: String? = "",
    @SerializedName("isOwner")
    val isOwner: Boolean? = false,
    @SerializedName("account")
    val account: Account = Account(),
    @SerializedName("storeName")
    val storeName: String? = "",
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("location")
    val location: Location = Location(),
    @SerializedName("categories")
    val categories: List<Category>? = emptyList(),
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerializedName("activitiesStatus")
    val activitiesStatus: String? = "RECENT_ACTIVITY",
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = ""
)

data class ReviewWriter(
    @SerializedName("userId")
    val userId: Int? = 0,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("socialType")
    val socialType: String? = "KAKAO",
    @SerializedName("medal")
    val medal: Medal? = Medal()
)