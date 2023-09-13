package com.threedollar.network.api

import com.threedollar.network.BuildConfig
import com.threedollar.network.data.kakao.KakaoTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KakaoLoginApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun refreshToken(
        @Field("refresh_token")
        refreshToken: String,
        @Field("client_id")
        clientId: String = BuildConfig.MAP_KEY,
        @Field("grant_type")
        grantType: String = "refresh_token",
    ): Response<KakaoTokenResponse>
}