package com.threedollar.network.data


import com.google.gson.annotations.SerializedName

data class ReportReasonsResponse(
    @SerializedName("reasons")
    val reasons: List<Reason>? = listOf()
)