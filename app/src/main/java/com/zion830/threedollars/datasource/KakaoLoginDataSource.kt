package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.response.kakao.KakaoTokenResponse
import retrofit2.Response

interface KakaoLoginDataSource {

    suspend fun refreshToken(refreshToken: String): Response<KakaoTokenResponse>
}