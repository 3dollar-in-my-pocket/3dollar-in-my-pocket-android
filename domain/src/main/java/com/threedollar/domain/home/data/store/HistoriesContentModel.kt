package com.threedollar.domain.home.data.store


data class HistoriesContentModel(
    val visit: VisitModel = VisitModel(),
    val visitor: VisitorModel = VisitorModel(),
)