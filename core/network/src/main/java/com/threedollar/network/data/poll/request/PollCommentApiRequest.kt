package com.threedollar.network.data.poll.request


import com.google.gson.annotations.SerializedName

data class PollCommentApiRequest(
    @SerializedName("content")
    val content: String
)