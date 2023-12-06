package com.threedollar.network.data.poll.request


import com.google.gson.annotations.SerializedName

data class PollReportCreateApiRequest(
    @SerializedName("reason")
    val reason: String, // string
    @SerializedName("reasonDetail")
    val reasonDetail: String? = null// string
)