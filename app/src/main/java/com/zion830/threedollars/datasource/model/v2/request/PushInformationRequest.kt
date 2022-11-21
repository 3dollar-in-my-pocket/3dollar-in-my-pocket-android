package com.zion830.threedollars.datasource.model.v2.request

import com.google.gson.annotations.SerializedName

data class PushInformationRequest(
    @SerializedName("pushPlatformType")
    val pushPlatformType: String = "FCM",
    @SerializedName("pushToken")
    val pushToken: String = ""
)