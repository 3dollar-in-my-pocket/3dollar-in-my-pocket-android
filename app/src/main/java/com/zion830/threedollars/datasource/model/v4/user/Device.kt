package com.zion830.threedollars.datasource.model.v4.user


import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("isSetupNotification")
    val isSetupNotification: Boolean = false,
)