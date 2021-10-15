package com.zion830.threedollars.repository.model.v2.request


import com.google.gson.annotations.SerializedName

data class NewReviewRequest(
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("storeId")
    val storeId: Int = 0
)