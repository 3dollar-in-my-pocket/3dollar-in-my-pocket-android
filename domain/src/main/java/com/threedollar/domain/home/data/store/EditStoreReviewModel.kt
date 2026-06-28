package com.threedollar.domain.home.data.store

data class EditStoreReviewModel(
    val contents: String,
    val createdAt: String,
    val rating: Int,
    val reviewId: Long,
    val status: String,
    val storeId: Int,
    val updatedAt: String,
    val userId: Int,
)
