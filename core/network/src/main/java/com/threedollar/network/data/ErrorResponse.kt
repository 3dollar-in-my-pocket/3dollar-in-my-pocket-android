package com.threedollar.network.data

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("ok")
    val ok: Boolean? = null,
    @SerializedName("resultCode")
    val resultCode: String? = null,
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("message")
    val message: String? = null,
)