package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class PollCommentCreateApiResponse(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("message")
    var message: String? = null, // string
    @SerializedName("resultCode")
    var resultCode: String? = null // string
) {
    data class Data(
        @SerializedName("id")
        var id: String? = null // string
    )
}