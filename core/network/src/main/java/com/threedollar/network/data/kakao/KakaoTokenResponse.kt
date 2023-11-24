package com.threedollar.network.data.kakao

import com.google.gson.annotations.SerializedName

data class KakaoTokenResponse(
    @SerializedName("token_type")
    val tokenType: String = "",
    @SerializedName("access_token")
    val accessToken: String = "",
    @SerializedName("expires_in")
    val expiresIn: Int = 0,
    @SerializedName("refresh_token")
    val refreshToken: String = "",
    @SerializedName("refresh_token_expires_in")
    val refreshTokenExpiresIn: Int = 0,
    @SerializedName("scope")
    val scope: String = ""
)