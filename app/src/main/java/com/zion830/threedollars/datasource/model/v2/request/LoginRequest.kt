package com.zion830.threedollars.datasource.model.v2.request


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("socialType")
    val socialType: String = "KAKAO",
    @SerializedName("token")
    val token: String = ""
)