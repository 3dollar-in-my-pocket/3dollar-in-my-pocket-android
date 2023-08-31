package com.zion830.threedollars.datasource.model.v4.feedback


import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("feedbackType")
    val feedbackType: String = "",
)