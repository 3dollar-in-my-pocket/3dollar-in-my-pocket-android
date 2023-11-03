package com.threedollar.network.request


import com.google.gson.annotations.SerializedName

data class ReportReviewRequest(
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("reasonDetail")
    val reasonDetail: String?
)