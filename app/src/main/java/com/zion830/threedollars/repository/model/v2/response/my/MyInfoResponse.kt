package com.zion830.threedollars.repository.model.v2.response.my


import com.google.gson.annotations.SerializedName

data class MyInfoResponse(
    @SerializedName("data")
    val data: User = User(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)