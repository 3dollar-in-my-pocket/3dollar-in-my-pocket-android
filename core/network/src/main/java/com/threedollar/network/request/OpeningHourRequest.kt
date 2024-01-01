package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class OpeningHourRequest(
    @SerializedName("endTime")
    val endTime: String? = null,
    @SerializedName("startTime")
    val startTime: String? = null,
)
