package com.home.domain.data.store


data class ReviewModel(
    val contents: String? = null,
    val createdAt: String = "",
    val rating: Int = 0,
    val reviewId: Int = 0,
    val status: String = "",
    val updatedAt: String = "",
    val isOwner : Boolean = false
)