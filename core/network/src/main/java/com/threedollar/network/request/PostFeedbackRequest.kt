package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class PostFeedbackRequest(
    @SerializedName("feedbackTypes")
    val feedbackTypes: List<String>
)