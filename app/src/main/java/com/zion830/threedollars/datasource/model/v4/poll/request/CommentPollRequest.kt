package com.zion830.threedollars.datasource.model.v4.poll.request

import com.google.gson.annotations.SerializedName

data class CommentPollRequest(
    @SerializedName("parentId")
    val parentId: String,
    @SerializedName("content")
    val content: String,
)