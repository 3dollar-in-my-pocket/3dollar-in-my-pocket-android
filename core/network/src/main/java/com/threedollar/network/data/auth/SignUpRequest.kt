package com.threedollar.network.data.auth

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "KAKAO",
    @SerializedName("token")
    val token: String = ""
)