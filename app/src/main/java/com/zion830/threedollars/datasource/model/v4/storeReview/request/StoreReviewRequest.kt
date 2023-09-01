package com.zion830.threedollars.datasource.model.v4.storeReview.request

data class StoreReviewRequest(
    val storeId: Int,
    val contents: String,
    val rating: Int,
)