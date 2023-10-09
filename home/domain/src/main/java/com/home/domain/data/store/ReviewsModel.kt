package com.home.domain.data.store


data class ReviewsModel(
    val contents: List<ReviewContentModel> = listOf(),
    val cursor: CursorModel = CursorModel()
)