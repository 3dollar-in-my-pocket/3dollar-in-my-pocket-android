package com.zion830.threedollars.datasource.model.v4.device

import com.google.gson.annotations.SerializedName

data class PushInformationRequest(
    @SerializedName("pushPlatformType")
    val pushPlatformType: String = "FCM",
    @SerializedName("pushToken")
    val pushToken: String = ""
)