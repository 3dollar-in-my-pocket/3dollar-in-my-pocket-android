package com.zion830.threedollars.datasource.model.v4.storeReview


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Cursor

data class StoreReviewResponse(
    @SerializedName("contents")
    val reviewModels: List<ReviewModel> = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor = Cursor()
)