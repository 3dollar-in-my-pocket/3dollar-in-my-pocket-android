package com.zion830.threedollars.repository.model.v2.request


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("socialType")
    val socialType: String = "",
    @SerializedName("token")
    val token: String = ""
)