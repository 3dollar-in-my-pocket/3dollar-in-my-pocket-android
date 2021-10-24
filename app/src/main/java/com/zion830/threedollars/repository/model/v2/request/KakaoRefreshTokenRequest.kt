package com.zion830.threedollars.repository.model.v2.request

import com.google.gson.annotations.SerializedName

data class KakaoRefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String = "",
    @SerializedName("grant_type")
    val grantType: String = "refresh_token",
    @SerializedName("client_id")
    val clientId: String = "a0ce3cecac62e2dedf50751d5abc5740",
)