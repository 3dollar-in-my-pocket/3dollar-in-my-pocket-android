package com.threedollar.network.data.auth

import com.google.gson.annotations.SerializedName

data class SignResponse(
    @SerializedName("data")
    val data: SignUser = SignUser(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class SignUser(
    @SerializedName("token")
    val token: String = "",
    @SerializedName("userId")
    val userId: Int = 0
)