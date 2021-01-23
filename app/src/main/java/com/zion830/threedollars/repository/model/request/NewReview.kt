package com.zion830.threedollars.repository.model.request


import com.google.gson.annotations.SerializedName

data class NewReview(
    @SerializedName("contents")
    val contents: String,
    @SerializedName("rating")
    val rating: Float
)