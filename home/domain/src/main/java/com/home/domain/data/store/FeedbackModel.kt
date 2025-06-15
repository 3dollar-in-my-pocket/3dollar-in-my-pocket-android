package com.home.domain.data.store

data class FeedbackModel(
    val feedbackType: FeedbackType,
    val description: String,
    val emoji: String,
    val count: Int,
    val ratio: Float
)