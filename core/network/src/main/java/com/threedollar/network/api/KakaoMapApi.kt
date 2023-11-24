package com.threedollar.network.api

import com.threedollar.network.BuildConfig
import com.threedollar.network.data.kakao.SearchAddressResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoMapApi {

    @GET("/v2/local/search/keyword.json")
    suspend fun searchAddress(
        @Query("query") query: String,
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Header("Authorization") authorization: String = "KakaoAK ${BuildConfig.MAP_KEY}"
    ): SearchAddressResponse
}