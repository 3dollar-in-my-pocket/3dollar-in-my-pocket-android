package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class StoreReviewRequest(
    @SerializedName("contents")
    val contents: String,
    @SerializedName("rating")
    val rating: Int? = null,
    @SerializedName("storeId")
    val storeId: Int
)