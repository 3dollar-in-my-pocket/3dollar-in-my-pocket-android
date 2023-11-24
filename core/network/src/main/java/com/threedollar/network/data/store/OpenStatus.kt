package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class OpenStatus(
    @SerializedName("openStartDateTime")
    val openStartDateTime: String? = "",
    @SerializedName("status")
    val status: String? = ""
)