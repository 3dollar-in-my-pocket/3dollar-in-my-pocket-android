package com.home.domain.data.store


data class ReviewModel(
    val contents: String? = null,
    val createdAt: String = "",
    val rating: Float = 0.0f,
    val reviewId: Int = 0,
    val status: ReviewStatusType = ReviewStatusType.POSTED,
    val updatedAt: String = "",
    val isOwner : Boolean = false,
    val images: List<ImageModel> = emptyList()
)