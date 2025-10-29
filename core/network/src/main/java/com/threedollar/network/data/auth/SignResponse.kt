package com.threedollar.network.data.auth

import com.google.gson.annotations.SerializedName

data class SignUser(
    @SerializedName("token")
    val token: String = "",
    @SerializedName("userId")
    val userId: Int = 0
)