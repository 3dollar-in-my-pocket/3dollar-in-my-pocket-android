package com.zion830.threedollars.datasource.model.v4.user


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String = "",
    @SerializedName("userId")
    val userId: Int = 0,
)