package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.store.StoreDisplayItemsResponse
import com.threedollar.network.request.StoreDisplayItemsRequest
import com.threedollar.network.sdui.model.screen.SDScreenModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface StoreApi {
    @GET("/api/v1/screen/store/{storeId}")
    suspend fun getScreenStore(
        @Path("storeId") storeId: Int,
        @Header("X-Device-Latitude") lat: Double,
        @Header("X-Device-Longitude") lng: Double,
    ): Response<BaseResponse<SDScreenModel>>

    @GET("/api/v1/store/{storeId}/display-items")
    suspend fun getStoreDisplayItems(
        @Path("storeId") storeId: Int,
        @Query("itemTypes") itemTypes: List<String>,
        @Header("X-Device-Latitude") lat: Double,
        @Header("X-Device-Longitude") lng: Double,
    ): Response<BaseResponse<StoreDisplayItemsResponse>>

    @POST("/api/v1/store/{storeId}/display-items/impression")
    suspend fun postStoreDisplayItemImpression(
        @Path("storeId") storeId: Int,
        @Body request: StoreDisplayItemsRequest,
    ): Response<BaseResponse<String>>
}
