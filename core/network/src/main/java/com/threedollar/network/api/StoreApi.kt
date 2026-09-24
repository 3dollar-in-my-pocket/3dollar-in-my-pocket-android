package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.store.StoreDisplayItemsResponse
import com.threedollar.network.request.StoreDisplayItemsRequest
import com.threedollar.common.sdui.model.screen.SDScreenModel
import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.network.request.StickerRequest
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("/api/v2/screen/store/{storeId}")
    suspend fun getStoreScreenV2(
        @Path("storeId") storeId: String,
        @Header("X-Device-Latitude") lat: Double?,
        @Header("X-Device-Longitude") lng: Double?,
    ): Response<BaseResponse<SDStoreScreenModel>>

    @POST("/api/v1/store/{storeId}/coupon/{couponId}/issue")
    suspend fun issueStoreCoupon(
        @Path("storeId") storeId: String,
        @Path("couponId") couponId: String,
    ): Response<BaseResponse<Any>>

    @PUT("/api/v1/issued-coupon/{issuedKey}/use")
    suspend fun useIssuedCoupon(
        @Path("issuedKey") issuedKey: String,
    ): Response<BaseResponse<String>>

    @PUT("/api/v1/store/{storeId}/news-post/{postId}/stickers")
    suspend fun putStorePostStickers(
        @Path("storeId") storeId: String,
        @Path("postId") postId: String,
        @Body request: StickerRequest,
    ): Response<BaseResponse<String>>

    @PUT("/api/v1/store/{storeId}/review/{reviewId}/stickers")
    suspend fun putStoreReviewStickers(
        @Path("storeId") storeId: String,
        @Path("reviewId") reviewId: String,
        @Body request: StickerRequest,
    ): Response<BaseResponse<String>>

    @DELETE("/api/v2/store/review/{reviewId}")
    suspend fun deleteStoreReview(
        @Path("reviewId") reviewId: String,
    ): Response<BaseResponse<String>>
}
