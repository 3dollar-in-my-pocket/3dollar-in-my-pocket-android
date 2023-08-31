package com.zion830.threedollars.datasource.model.v4.feedback


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Store

data class Content(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("feedbacks")
    val feedbacks: List<Feedback> = listOf(),
    @SerializedName("store")
    val store: Store = Store()
)