package com.threedollar.common.base


import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("ok")
    val ok : Boolean = false,
    @SerializedName("data")
    val data: T,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("resultCode")
    val resultCode: String? = ""
)