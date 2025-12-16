package com.threedollar.network.data

import com.google.gson.annotations.SerializedName

data class AppStatusResponse(
    @SerializedName("osPlatform")
    val osPlatform: String,

    @SerializedName("currentVersion")
    val currentVersion: String,

    @SerializedName("forceUpdate")
    val forceUpdate: AppForceUpdateResponse
)

data class AppForceUpdateResponse(
    @SerializedName("enabled")
    val enabled: Boolean,

    @SerializedName("title")
    val title: String?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("linkUrl")
    val linkUrl: String?
)