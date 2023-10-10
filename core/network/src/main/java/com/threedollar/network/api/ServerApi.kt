package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.store.BossStoreResponse
import com.threedollar.network.data.store.DeleteResultResponse
import com.threedollar.network.data.store.UserStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PostFeedbackRequest
import com.threedollar.network.request.PostStoreVisitRequest
import com.threedollar.network.request.PushInformationRequest
import retrofit2.Response
import retrofit2.http.*

interface ServerApi {

    // User
    @GET("/api/v2/user/me")
    suspend fun getMyInfo(): BaseResponse<UserResponse>

    @PUT("/api/v1/user/me/marketing-consent")
    suspend fun putMarketingConsent(@Body marketingConsentRequest: MarketingConsentRequest): BaseResponse<String>

    // Store
    @GET("/api/v4/stores/around")
    suspend fun getAroundStores(
        @Query("distanceM") distanceM: Double = 100000.0,
        @Query("categoryIds") categoryIds: Array<String>? = null,
        @Query("targetStores") targetStores: Array<String>? = null,
        @Query("sortType") sortType: String,
        @Query("filterCertifiedStores") filterCertifiedStores: Boolean? = null,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Header("X-Device-Latitude") deviceLatitude: Double,
        @Header("X-Device-Longitude") deviceLongitude: Double,
    ): BaseResponse<AroundStoreResponse>

    @GET("/api/v4/boss-store/{bossStoreId}")
    suspend fun getBossStoreDetail(
        @Path("bossStoreId") bossStoreId: String,
        @Header("X-Device-Latitude") deviceLatitude: Double,
        @Header("X-Device-Longitude") deviceLongitude: Double,
    ): BaseResponse<BossStoreResponse>

    @GET("/api/v4/store/{storeId}")
    suspend fun getUserStoreDetail(
        @Path("storeId") storeId: Int,
        @Header("X-Device-Latitude") deviceLatitude: Double,
        @Header("X-Device-Longitude") deviceLongitude: Double,
        @Query("storeImagesCount") storeImagesCount: Int?,
        @Query("reviewsCount") reviewsCount: Int?,
        @Query("visitHistoriesCount") visitHistoriesCount: Int?,
        @Query("filterVisitStartDate") filterVisitStartDate: String,
    ): BaseResponse<UserStoreResponse>

    @DELETE("/api/v2/store/{storeId}")
    suspend fun deleteStore(
        @Path("storeId") storeId: Int,
        @Query("deleteReasonType") deleteReasonType: String = "WRONG_CONTENT",
    ): BaseResponse<DeleteResultResponse>

    @POST("/api/v2/store/visit")
    suspend fun postStoreVisit(@Body postStoreVisitRequest: PostStoreVisitRequest): BaseResponse<String>


    // Device
    @POST("/api/v1/device")
    suspend fun postPushInformation(@Body informationRequest: PushInformationRequest): BaseResponse<String>

    // advertisement
    @GET("/api/v1/advertisements")
    suspend fun getAdvertisements(@Query("position") position: String): BaseResponse<List<AdvertisementResponse>>

    // favorite
    @PUT("/api/v1/favorite/subscription/store/target/{storeType}/{storeId}")
    suspend fun putFavorite(@Path("storeType") storeType: String, @Path("storeId") storeId: String): BaseResponse<String>

    @DELETE("/api/v1/favorite/subscription/store/target/{storeType}/{storeId}")
    suspend fun deleteFavorite(@Path("storeType") storeType: String, @Path("storeId") storeId: String): BaseResponse<String>

    // feedback
    @POST("/api/v1/feedback/{targetType}/target/{targetId}")
    suspend fun postFeedback(
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: String,
        @Body postFeedbackRequest: PostFeedbackRequest,
    ): BaseResponse<String>

    @GET("/api/v1/feedback/{targetType}/target/{targetId}/full")
    suspend fun getFeedbackFull(@Path("targetType") targetType: String, @Path("targetId") targetId: String): BaseResponse<List<FeedbackCountResponse>>

    @GET("/api/v1/feedback/{targetType}/types")
    suspend fun getFeedbackTypes(@Path("targetType") targetType: String): BaseResponse<List<FeedbackTypeResponse>>

}