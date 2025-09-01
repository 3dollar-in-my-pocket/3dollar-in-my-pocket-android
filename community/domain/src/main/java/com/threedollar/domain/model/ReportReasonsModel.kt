package com.threedollar.domain.model

data class ReportReasonsModel(
    val reasons: List<ReportReason>
)

data class ReportReason(
    val id: String,
    val title: String,
    val description: String?,
    val hasDetail: Boolean
)