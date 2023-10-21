package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.store.*
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PostFeedbackRequest
import com.threedollar.network.request.PostStoreVisitRequest
import com.threedollar.network.request.PushInformationRequest
import com.threedollar.network.request.StoreReviewRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ServerApi {

    // User
    @GET("/api/v2/user/me")
    suspend fun getMyInfo(): Response<BaseResponse<UserResponse>>

    @PUT("/api/v1/user/me/marketing-consent")
    suspend fun putMarketingConsent(@Body marketingConsentRequest: MarketingConsentRequest): Response<BaseResponse<String>>

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
    ): Response<BaseResponse<AroundStoreResponse>>

    @GET("/api/v4/boss-store/{bossStoreId}")
    suspend fun getBossStoreDetail(
        @Path("bossStoreId") bossStoreId: String,
        @Header("X-Device-Latitude") deviceLatitude: Double,
        @Header("X-Device-Longitude") deviceLongitude: Double,
    ): Response<BaseResponse<BossStoreResponse>>

    @GET("/api/v4/store/{storeId}")
    suspend fun getUserStoreDetail(
        @Path("storeId") storeId: Int,
        @Header("X-Device-Latitude") deviceLatitude: Double,
        @Header("X-Device-Longitude") deviceLongitude: Double,
        @Query("storeImagesCount") storeImagesCount: Int?,
        @Query("reviewsCount") reviewsCount: Int?,
        @Query("visitHistoriesCount") visitHistoriesCount: Int?,
        @Query("filterVisitStartDate") filterVisitStartDate: String,
    ): Response<BaseResponse<UserStoreResponse>>

    @DELETE("/api/v2/store/{storeId}")
    suspend fun deleteStore(
        @Path("storeId") storeId: Int,
        @Query("deleteReasonType") deleteReasonType: String = "WRONG_CONTENT",
    ): Response<BaseResponse<DeleteResultResponse>>

    @DELETE("/api/v2/store/image/{storeImageId}")
    suspend fun deleteImage(@Path("storeImageId") imageId: Int): Response<BaseResponse<String>>

    @POST("/api/v2/store/visit")
    suspend fun postStoreVisit(@Body postStoreVisitRequest: PostStoreVisitRequest): Response<BaseResponse<String>>


    // Device
    @POST("/api/v1/device")
    suspend fun postPushInformation(@Body informationRequest: PushInformationRequest): Response<BaseResponse<String>>

    // advertisement
    @GET("/api/v1/advertisements")
    suspend fun getAdvertisements(@Query("position") position: String): Response<BaseResponse<List<AdvertisementResponse>>>

    // favorite
    @PUT("/api/v1/favorite/subscription/store/target/{storeType}/{storeId}")
    suspend fun putFavorite(@Path("storeType") storeType: String, @Path("storeId") storeId: String): Response<BaseResponse<String>>

    @DELETE("/api/v1/favorite/subscription/store/target/{storeType}/{storeId}")
    suspend fun deleteFavorite(@Path("storeType") storeType: String, @Path("storeId") storeId: String): Response<BaseResponse<String>>

    // feedback
    @POST("/api/v1/feedback/{targetType}/target/{targetId}")
    suspend fun postFeedback(
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: String,
        @Body postFeedbackRequest: PostFeedbackRequest,
    ): Response<BaseResponse<String>>

    @GET("/api/v1/feedback/{targetType}/target/{targetId}/full")
    suspend fun getFeedbackFull(
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: String,
    ): Response<BaseResponse<List<FeedbackCountResponse>>>

    @GET("/api/v1/feedback/{targetType}/types")
    suspend fun getFeedbackTypes(@Path("targetType") targetType: String): Response<BaseResponse<List<FeedbackTypeResponse>>>

    @POST("/api/v2/store/images")
    @Multipart
    suspend fun saveImages(@Part images: List<MultipartBody.Part>, @Query("storeId") storeId: Int): Response<BaseResponse<List<SaveImagesResponse>>>

    @GET("/api/v4/store/{storeId}/images")
    suspend fun getStoreImages(
        @Path("storeId") storeId: Int,
        @Query("size") size: Int,
        @Query("cursor") cursor: String?,
    ): Response<BaseResponse<ImageResponse>>

    @POST("/api/v3/store/review")
    suspend fun postStoreReview(@Body storeReviewRequest: StoreReviewRequest): Response<BaseResponse<ReviewContent>>

    @PUT("/api/v2/store/review/{reviewId}")
    suspend fun putStoreReview(
        @Path("reviewId") reviewId: Int,
        @Body storeReviewRequest: StoreReviewRequest,
    ): Response<BaseResponse<EditStoreReviewResponse>>

    @GET("/api/v4/store/{storeId}/reviews")
    suspend fun getStoreReview(
        @Path("storeId") storeId: Int,
        @Query("size") size: Int = 20,
        @Query("cursor") cursor: String?,
        @Query("sort") sort: String,
    ): Response<BaseResponse<Reviews>>
}