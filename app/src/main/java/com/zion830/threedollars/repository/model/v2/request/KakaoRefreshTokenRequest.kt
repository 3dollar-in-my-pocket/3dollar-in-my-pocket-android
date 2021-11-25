package com.zion830.threedollars.repository.model.v2.request

import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.BuildConfig

data class KakaoRefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String = "",
    @SerializedName("grant_type")
    val grantType: String = "refresh_token",
    @SerializedName("client_id")
    val clientId: String = BuildConfig.KAKAO_KEY,
)