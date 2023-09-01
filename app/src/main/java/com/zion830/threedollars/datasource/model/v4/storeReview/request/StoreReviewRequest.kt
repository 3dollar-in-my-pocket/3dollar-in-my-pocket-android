package com.zion830.threedollars.datasource.model.v4.storeReview.request

import com.google.gson.annotations.SerializedName

data class StoreReviewRequest(
    @SerializedName("storeId")
    val storeId: Int,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("rating")
    val rating: Int,
)