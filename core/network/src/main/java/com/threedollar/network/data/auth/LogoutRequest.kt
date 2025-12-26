package com.threedollar.network.data.auth

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("logoutDevice")
    val logoutDevice: LogoutDeviceRequest
)

data class LogoutDeviceRequest(
    val pushPlatform: String = "FCM",
    val pushToken: String
)
