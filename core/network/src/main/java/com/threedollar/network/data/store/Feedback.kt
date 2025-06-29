package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("feedbackType")
    val feedbackType: String? = "",

    @SerializedName("description")
    val description: String? = "",

    @SerializedName("emoji")
    val emoji: String? = "",

    @SerializedName("count")
    val count: Int? = 0,

    @SerializedName("ratio")
    val ratio: Float? = 0.0f
)