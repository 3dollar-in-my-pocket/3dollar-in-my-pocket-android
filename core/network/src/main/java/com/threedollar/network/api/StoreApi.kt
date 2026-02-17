package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.sdui.model.screen.SDScreen
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface StoreApi {
    @GET("/api/v1/screen/store/{storeId}")
    suspend fun getScreenStore(
        @Path("storeId") storeId: Int,
        @Header("X-Device-Latitude") lat: Double,
        @Header("X-Device-Longitude") lng: Double,
    ): Response<BaseResponse<SDScreen>>
}
