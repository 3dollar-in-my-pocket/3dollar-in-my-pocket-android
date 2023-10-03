package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("feedbackType")
    val feedbackType: String = "",
    @SerializedName("ratio")
    val ratio: Int = 0
)