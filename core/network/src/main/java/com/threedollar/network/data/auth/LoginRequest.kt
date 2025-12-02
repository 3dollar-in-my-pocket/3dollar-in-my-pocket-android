package com.threedollar.network.data.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("socialType")
    val socialType: String = "KAKAO",
    @SerializedName("token")
    val token: String = ""
)