package com.threedollar.domain.home.data.store


data class ReviewsModel(
    val contents: List<ReviewContentModel> = listOf(),
    val cursor: CursorModel = CursorModel()
)