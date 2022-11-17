package com.zion830.threedollars.datasource.model.v2.request

import com.google.gson.annotations.SerializedName

data class PushInformationSettingRequest(
    @SerializedName("pushSettings")
    val pushSettings: List<String> = listOf("ADVERTISEMENT")
)