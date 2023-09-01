package com.zion830.threedollars.datasource.model.v4.feedback.request

import com.google.gson.annotations.SerializedName

data class FeedbackTypes(
    @SerializedName("feedbackTypes")
    val feedbackTypes : List<String> = listOf()
)