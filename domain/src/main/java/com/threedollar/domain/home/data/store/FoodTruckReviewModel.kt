package com.threedollar.domain.home.data.store

data class FoodTruckReviewModel(
        val count: Int,
        val feedbackType: String?,
        val ratio: Double,
        val description: String?,
        val emoji: String?
    )