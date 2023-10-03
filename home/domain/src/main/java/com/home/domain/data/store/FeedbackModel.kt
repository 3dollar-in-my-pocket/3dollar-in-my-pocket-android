package com.home.domain.data.store

data class FeedbackModel(
    val count: Int = 0,
    val feedbackType: FeedbackType,
    val ratio: Int = 0
)