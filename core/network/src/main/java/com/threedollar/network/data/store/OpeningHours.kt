package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class OpeningHours(
    @SerializedName("endTime")
    val endTime: String? = "",
    @SerializedName("startTime")
    val startTime: String? = ""
)