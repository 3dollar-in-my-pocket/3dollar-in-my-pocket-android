package com.threedollar.domain.home.data.store


data class HistoriesModel(
    val contents: List<HistoriesContentModel> = listOf(),
    val cursor: CursorModel = CursorModel()
)