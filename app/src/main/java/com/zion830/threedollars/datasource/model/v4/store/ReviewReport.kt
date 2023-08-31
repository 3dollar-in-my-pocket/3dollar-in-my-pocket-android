package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class ReviewReport(
    @SerializedName("reportedByMe")
    val reportedByMe: Boolean = false,
)