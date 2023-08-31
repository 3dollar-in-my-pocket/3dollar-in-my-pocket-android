package com.zion830.threedollars.datasource.model.v4.feedback


import com.google.gson.annotations.SerializedName

data class FeedbackCountResponse(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("feedbackType")
    val feedbackType: String = "",
    @SerializedName("ratio")
    val ratio: Int = 0,
)