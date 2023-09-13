package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class PollDefaultResponse(
    @SerializedName("data")
    var `data`: String? = null, // string
    @SerializedName("message")
    var message: String? = null, // string
    @SerializedName("resultCode")
    var resultCode: String? = null // string
)