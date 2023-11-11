package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.neighborhood.GetNeighborhoodsResponse
import com.threedollar.network.data.neighborhood.GetPopularStoresResponse
import com.threedollar.network.data.poll.request.PollChoiceApiRequest
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse
import com.threedollar.network.data.store.*
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ServerApi {

    @POST("/api/v1/poll")
    suspend fun createPoll(@Body pollCreateApiRequest: PollCreateApiRequest): BaseResponse<PollCreateApiResponse>

    @GET("/api/v1/poll/{pollId}")
    suspend fun getPollId(@Path("pollId") id: String): BaseResponse<GetPollResponse>

    @PUT("/api/v1/poll/{pollId}/choice")
    suspend fun putPollChoice(@Path("pollId") id: String, @Body pollChoiceApiRequest: PollChoiceApiRequest): BaseResponse<String>

    @DELETE("/api/v1/poll/{pollId}/choice")
    suspend fun deletePollChoice(@Path("pollId") id: String): BaseResponse<String>

    @POST("/api/v1/poll/{pollId}/report")
    suspend fun reportPoll(@Path("pollId") id: String): BaseResponse<String>

    @GET("/api/v1/poll/categories")
    suspend fun getPollCategories(): BaseResponse<PollCategoryApiResponse>

    @GET("/api/v1/polls")
    suspend fun getPollList(
        @Query("categoryId") categoryId: String,
        @Query("sortType") sortType: String?,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int = 20,
    ): Response<BaseResponse<GetPollListResponse>>

    @GET("/api/v1/user/poll/policy")
    suspend fun getPollPolicy(): BaseResponse<PollPolicyApiResponse>

    @GET("/api/v1/user/polls")
    suspend fun getUserPollList(@Query("cursor") cursor: Int?, @Query("size") size: Int = 20): BaseResponse<GetUserPollListResponse>

    @POST("/api/v1/poll/{pollId}/comment")
    suspend fun createPollComment(
        @Path("pollId") id: String,
        @Body pollCommentApiRequest: PollCommentApiRequest
    ): BaseResponse<PollCommentCreateApiResponse>

    @DELETE("/api/v1/poll/{pollId}/comment/{commentId}")
    suspend fun deletePollComment(@Path("pollId") pollId: String, @Path("commentId") commentId: String): BaseResponse<String>

    @PATCH("/api/v1/poll/{pollId}/comment/{commentId}")
    suspend fun editPollComment(@Path("pollId") pollId: String, @Path("commentId") commentId: String): BaseResponse<String>

    @POST("/api/v1/poll/{pollId}/comment/{commentId}/report")
    suspend fun reportPollComment(@Path("pollId") pollId: String, @Path("commentId") commentId: String): BaseResponse<String>

    @GET("/api/v1/poll/{pollId}/comments")
    suspend fun getPollCommentList(
        @Path("pollId") id: String,
        @Query("cursor") cursor: Int?,
        @Query("size") size: Int = 20,
    ): BaseResponse<GetPollCommentListResponse>

    @GET("/api/v1/neighborhood/popular-store/neighborhoods")
    suspend fun getNeighborhoods(): BaseResponse<GetNeighborhoodsResponse>

    @GET("/api/v1/neighborhood/popular-stores")
    suspend fun getPopularStores(
        @Query("criteria") criteria: String,
        @Query("district") district: String,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int = 20,
    ): Response<BaseResponse<GetPopularStoresResponse>>

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

    @GET("/api/v1/stores/near/exists")
    suspend fun getStoreNearExists(
        @Query("distance") distance: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
    ): Response<BaseResponse<StoreNearExistResponse>>

    @POST("/api/v2/store")
    suspend fun postUserStore(@Body userStoreRequest: UserStoreRequest): Response<BaseResponse<PostUserStoreResponse>>

    @PUT("/api/v2/store/{storeId}")
    suspend fun putUserStore(@Body userStoreRequest: UserStoreRequest, @Path("storeId") storeId: Int): Response<BaseResponse<PostUserStoreResponse>>

    @POST("/api/v1/store/{storeId}/review/{reviewId}/report")
    suspend fun reportStoreReview(
        @Path("storeId") storeId: Int,
        @Path("reviewId") reviewId: Int,
        @Body reportReviewRequest: ReportReviewRequest,
    ): Response<BaseResponse<String>>

    @GET("/api/v1/report/group/{group}/reasons")
    suspend fun getReportReasons(@Path("group") group: String): Response<BaseResponse<ReportReasonsResponse>>
}