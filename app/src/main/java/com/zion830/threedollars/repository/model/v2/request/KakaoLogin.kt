package com.zion830.threedollars.repository.model.v2.request

import com.google.gson.annotations.SerializedName


data class KakaoLogin(
    @SerializedName("grant_type")
    val grantType: String = "authorization_code",
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("redirect_uri")
    val redirectUri: String = "",
    @SerializedName("code")
    val code: String
)