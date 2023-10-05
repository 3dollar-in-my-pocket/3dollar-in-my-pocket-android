package com.threedollar.network.data.feedback

import com.google.gson.annotations.SerializedName


data class FeedbackCountResponse(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("feedbackType")
    val feedbackType: String = "",
    @SerializedName("ratio")
    val ratio: Double = 0.0
)