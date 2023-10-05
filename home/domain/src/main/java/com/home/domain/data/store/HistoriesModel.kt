package com.home.domain.data.store


data class HistoriesModel(
    val contents: List<HistoriesContentModel> = listOf(),
    val cursor: CursorModel = CursorModel()
)