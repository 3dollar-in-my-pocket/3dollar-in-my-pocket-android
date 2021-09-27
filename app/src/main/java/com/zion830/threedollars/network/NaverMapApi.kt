package com.zion830.threedollars.network

import com.zion830.threedollars.repository.model.response.SearchAddressResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapApi {

    @GET("map-geocode/v2/geocode")
    suspend fun searchAddress(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String = "g3xhgeea6p",
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String = "iU9znhSI0u35yAgEktAmbhYwk7xozIjyWlHstvTX",
        @Query("query") query: String,
    ): SearchAddressResponse
}