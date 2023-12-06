package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class PollCommentCreateApiResponse(
    @SerializedName("id")
    var id: String? = null // string
)