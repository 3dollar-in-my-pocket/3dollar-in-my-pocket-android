package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class PushInformationRequest(
    @SerializedName("pushToken")
    val pushToken: String,
    @SerializedName("pushPlatformType")
    val pushPlatformType: String = "FCM"
)