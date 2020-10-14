package com.zion830.threedollars.network

import com.zion830.threedollars.repository.model.response.KakaoLoginResponse
import retrofit2.http.GET

interface KakaoApi {

    // 카카오 로그인
    @GET("/v1/user/access_token_info")
    suspend fun getKakaoTokenInfo(): KakaoLoginResponse
}