package com.zion830.threedollars.datasource.model.v4.report


import com.google.gson.annotations.SerializedName

data class ReportReasonResponse(
    @SerializedName("reasons")
    val reasons: List<Reason> = listOf(),
)