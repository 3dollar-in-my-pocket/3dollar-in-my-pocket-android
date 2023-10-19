package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class EditStoreReviewResponse(
    @SerializedName("contents")
    val contents: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("rating")
    val rating: Int? = null,
    @SerializedName("reviewId")
    val reviewId: Int? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("storeId")
    val storeId: Int? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("userId")
    val userId: Int? = null,
)