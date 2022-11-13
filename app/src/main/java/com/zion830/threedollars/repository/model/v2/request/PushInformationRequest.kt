package com.zion830.threedollars.repository.model.v2.request

import com.google.gson.annotations.SerializedName

data class PushInformationRequest(
    @SerializedName("pushPlatformType")
    val pushPlatformType: String = "FCM",
    @SerializedName("pushSettings")
    val pushSettings: List<String> = listOf(),
    @SerializedName("pushToken")
    val pushToken: String = ""
)