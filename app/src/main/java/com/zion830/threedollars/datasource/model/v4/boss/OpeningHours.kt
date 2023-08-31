package com.zion830.threedollars.datasource.model.v4.boss


import com.google.gson.annotations.SerializedName

data class OpeningHours(
    @SerializedName("endTime")
    val endTime: String = "",
    @SerializedName("startTime")
    val startTime: String = "",
)