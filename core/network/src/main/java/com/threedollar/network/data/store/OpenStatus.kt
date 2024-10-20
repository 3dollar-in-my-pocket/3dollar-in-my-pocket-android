package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class OpenStatus(
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("openStartDateTime")
    val openStartDateTime: String? = "",
    @SerializedName("isOpening")
    val isOpening: Boolean = false
)