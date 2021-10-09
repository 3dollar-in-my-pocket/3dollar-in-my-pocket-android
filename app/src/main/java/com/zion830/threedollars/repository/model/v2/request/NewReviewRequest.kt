package com.zion830.threedollars.repository.model.v2.request


import com.google.gson.annotations.SerializedName

data class NewReviewRequest(
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("storeId")
    val storeId: Int = 0
)