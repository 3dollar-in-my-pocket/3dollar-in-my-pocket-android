package com.zion830.threedollars.network

import com.zion830.threedollars.repository.model.v2.response.kakao.SearchAddressResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface KakaoMapApi {

    @GET("/v2/local/search/keyword.json")
    suspend fun searchAddress(
        @Query("query") query: String,
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Header("Authorization") authorization: String = "KakaoAK 5bbbafb84c73c6be5b181b6f3d514129"
    ): SearchAddressResponse
}