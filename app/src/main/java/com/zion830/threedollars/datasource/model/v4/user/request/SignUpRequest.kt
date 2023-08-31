package com.zion830.threedollars.datasource.model.v4.user.request


import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("socialType")
    val socialType: String = "KAKAO",
    @SerializedName("token")
    val token: String = ""
)