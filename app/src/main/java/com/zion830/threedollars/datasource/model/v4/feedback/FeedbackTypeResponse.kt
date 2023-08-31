package com.zion830.threedollars.datasource.model.v4.feedback


import com.google.gson.annotations.SerializedName

data class FeedbackTypeResponse(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("emoji")
    val emoji: String = "",
    @SerializedName("feedbackType")
    val feedbackType: String = "",
)