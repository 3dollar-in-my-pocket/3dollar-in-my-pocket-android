package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.response.kakao.KakaoTokenResponse
import com.zion830.threedollars.network.KakaoLoginApi
import retrofit2.Response
import javax.inject.Inject

class KakaoLoginDataSourceImpl @Inject constructor(private val kakaoLoginApi: KakaoLoginApi) :
    KakaoLoginDataSource {

    override suspend fun refreshToken(
        refreshToken: String,
    ): Response<KakaoTokenResponse> = kakaoLoginApi.refreshToken(refreshToken)
}