package com.threedollar.network.data.poll.response


import com.google.gson.annotations.SerializedName

data class PollPolicyApiResponse(
    @SerializedName("createPolicy")
    var createPolicy: CreatePolicy? = null
) {
    data class CreatePolicy(
        @SerializedName("currentCount")
        var currentCount: Int? = null, // 0
        @SerializedName("limitCount")
        var limitCount: Int? = null, // 0
        @SerializedName("pollRetentionDays")
        var pollRetentionDays: Int? = null
    )
}