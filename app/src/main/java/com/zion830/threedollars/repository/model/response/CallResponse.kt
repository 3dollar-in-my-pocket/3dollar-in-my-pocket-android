package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class CallResponse(
    @SerializedName("data")
    val data: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)