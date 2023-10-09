package com.home.domain.data.store


data class VisitsModel(
    val counts: CountsModel = CountsModel(),
    val histories: HistoriesModel = HistoriesModel()
)