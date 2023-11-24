package com.zion830.threedollars.datasource

import com.threedollar.network.api.KakaoLoginApi
import com.threedollar.network.data.kakao.KakaoTokenResponse
import retrofit2.Response
import javax.inject.Inject

class KakaoLoginDataSourceImpl @Inject constructor(private val kakaoLoginApi: KakaoLoginApi) :
    KakaoLoginDataSource {

    override suspend fun refreshToken(
        refreshToken: String,
    ): Response<KakaoTokenResponse> = kakaoLoginApi.refreshToken(refreshToken)
}