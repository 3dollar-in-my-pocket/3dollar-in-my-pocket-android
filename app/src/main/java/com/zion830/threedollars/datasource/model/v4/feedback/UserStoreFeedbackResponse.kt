package com.zion830.threedollars.datasource.model.v4.feedback


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Cursor

data class UserStoreFeedbackResponse(
    @SerializedName("contents")
    val contents: List<Content> = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor = Cursor(),
)