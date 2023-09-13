package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class PollPolicyApiResponse(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("message")
    var message: String? = null, // string
    @SerializedName("resultCode")
    var resultCode: String? = null // string
) {
    data class Data(
        @SerializedName("createPolicy")
        var createPolicy: CreatePolicy? = null
    ) {
        data class CreatePolicy(
            @SerializedName("currentCount")
            var currentCount: Int? = null, // 0
            @SerializedName("limitCount")
            var limitCount: Int? = null // 0
        )
    }
}