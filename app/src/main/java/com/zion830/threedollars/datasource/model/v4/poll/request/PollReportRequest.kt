package com.zion830.threedollars.datasource.model.v4.poll.request

import com.google.gson.annotations.SerializedName

data class PollReportRequest(
    @SerializedName("reason")
    val reason: String,
    @SerializedName("reasonDetail")
    val reasonDetail: String,
)