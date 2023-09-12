package com.zion830.threedollars.datasource

import com.threedollar.network.data.kakao.KakaoTokenResponse
import retrofit2.Response

interface KakaoLoginDataSource {

    suspend fun refreshToken(refreshToken: String): Response<KakaoTokenResponse>
}