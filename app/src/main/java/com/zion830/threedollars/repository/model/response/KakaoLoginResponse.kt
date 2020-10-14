package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class KakaoLoginResponse(
    @SerializedName("app_id")
    val appId: Int,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("id")
    val id: Int
)