package com.home.domain.data.store


data class HistoriesContentModel(
    val visit: VisitModel = VisitModel(),
    val visitor: VisitorModel = VisitorModel(),
)