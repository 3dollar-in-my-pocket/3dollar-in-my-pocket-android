package com.zion830.threedollars.datasource.model.v4.poll


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Cursor

data class CommentPollResponse(
    @SerializedName("contents")
    val commentPollModels: List<CommentPollModel> = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor = Cursor()
)