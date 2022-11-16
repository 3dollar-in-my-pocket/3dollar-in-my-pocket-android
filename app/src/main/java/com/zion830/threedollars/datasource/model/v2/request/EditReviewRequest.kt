package com.zion830.threedollars.datasource.model.v2.request


import com.google.gson.annotations.SerializedName

data class EditReviewRequest(
    @SerializedName("contents")
    val contents: String = "",
    @SerializedName("rating")
    val rating: Float = 0f
)