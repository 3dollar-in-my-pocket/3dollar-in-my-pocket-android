package com.zion830.threedollars.network

import com.zion830.threedollars.repository.model.request.KakaoRefreshTokenRequest
import com.zion830.threedollars.repository.model.v2.response.kakao.KakaoTokenResponse
import retrofit2.Response
import retrofit2.http.*

interface KakaoLoginApi {

    @POST("/oauth/token")
    suspend fun refreshToken(
        @Body kakaoRefreshToken: KakaoRefreshTokenRequest
    ): Response<KakaoTokenResponse>
}