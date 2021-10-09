package com.zion830.threedollars.repository.model.v2.response


import com.google.gson.annotations.SerializedName

data class BasicResponse(
    @SerializedName("data")
    val `data`: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)