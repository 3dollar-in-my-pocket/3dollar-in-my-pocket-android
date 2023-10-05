package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class ReviewReport(
    @SerializedName("reportedByMe")
    val reportedByMe: Boolean = false,
)