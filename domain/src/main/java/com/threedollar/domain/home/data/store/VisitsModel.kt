package com.threedollar.domain.home.data.store


data class VisitsModel(
    val counts: CountsModel = CountsModel(),
    val histories: HistoriesModel = HistoriesModel()
)