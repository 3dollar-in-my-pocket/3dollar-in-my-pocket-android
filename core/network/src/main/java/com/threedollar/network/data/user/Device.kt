package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("isSetupNotification")
    val isSetupNotification: Boolean = false
)