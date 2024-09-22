package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName
import com.threedollar.network.data.store.Cursor

data class MyFeedbacksResponse(
    @SerializedName("contents")
    val contents: List<MyFeedbacksData> = emptyList(),
    @SerializedName("cursor")
    val cursor: Cursor
)

data class MyFeedbacksData(
    @SerializedName("store")
    val store: Store = Store(),
    @SerializedName("date")
    val date: String = "",
    @SerializedName("feedbacks")
    val feedbacks: List<MyFeedbacksFeedback>? = emptyList()
)

data class MyFeedbacksFeedback(
    @SerializedName("feedbackType")
    val feedbackType: String? = "HANDS_ARE_FAST",
    @SerializedName("count")
    val count: Int = 0
)
