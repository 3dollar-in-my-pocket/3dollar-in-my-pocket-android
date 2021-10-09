package com.zion830.threedollars.network

import com.zion830.threedollars.repository.model.v2.request.*
import com.zion830.threedollars.repository.model.v2.response.*
import com.zion830.threedollars.repository.model.v2.response.my.*
import com.zion830.threedollars.repository.model.v2.response.store.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import zion830.com.common.base.BaseResponse

interface NewServiceApi {

    // 리뷰
    @POST("/api/v2/store/review")
    fun saveReview(
        @Body newReviewRequest: NewReviewRequest
    ): Response<NewReviewResponse>

    @GET("/api/v2/store/reviews/me")
    fun getMyReviews(
        @Query("cachingTotalElements") cachingTotalElements: Int = 100,
        @Query("cursor") cursor: Int = 0,
        @Query("size") size: Int = 100,
    ): Response<MyReviewResponse>

    @PUT("/api/v2/store/review/{reviewId}")
    fun editReview(
        @Path("reviewId") reviewId: Int,
        @Body editReviewRequest: EditReviewRequest
    ): Response<NewReviewResponse>

    @DELETE("/api/v2/store/review/{reviewId}")
    fun deleteReview(
        @Path("reviewId") reviewId: Int,
    ): Response<BaseResponse<String>>

    // 가게
    @POST("/api/v2/store")
    fun saveStore(
        @Path("storeId") storeId: Int,
        @Body newStoreRequest: NewStoreRequest
    ): Response<NewStoreResponse>

    @PUT("/api/v2/store/{storeId}")
    fun editStore(
        @Path("storeId") storeId: Int,
        @Body editStoreRequest: NewStoreRequest
    ): Response<NewStoreResponse>

    @DELETE("/api/v2/store/{storeId}")
    fun deleteStore(
        @Path("storeId") storeId: Int,
        @Query("deleteReasonType") deleteReasonType: String = "WRONG_CONTENT"
    ): Response<DeleteStoreResponse>

    @DELETE("/api/v2/store/image/{imageId}")
    fun deleteImage(@Path("imageId") imageId: Int): Response<BaseResponse<String>>

    @POST("/api/v2/store/images")
    @Multipart
    fun saveImages(
        @Part images: List<MultipartBody.Part>,
        @Query("storeId") storeId: Int
    ): Response<AddImageResponse>

    // 가게 검색
    @GET("/api/v2/store")
    fun getStoreInfo(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("storeId") storeId: Int,
    ): Response<StoreDetailResponse>

    @GET("/api/v2/stores/distance")
    fun getStoreByDistance(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("category") category: String,
    ): Response<StoreByDistanceResponse>

    @GET("/api/v2/stores/review")
    fun getStoreByRating(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("category") category: String,
    ): Response<StoreByRatingResponse>

    @GET("/api/v2/stores/near")
    fun getNearStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("distance") distance: Double = 10000.0,
    ): Response<NearStoreResponse>

    @GET("/api/v2/store/{storeId}/images")
    fun getStoreImages(
        @Query("storeId") storeId: Int,
    ): Response<AddImageResponse>

    @GET("/api/v2/stores/me")
    fun getMyStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("cachingTotalElements") cachingTotalElements: Int = 100,
        @Query("cursor") cursor: Int = 0,
        @Query("size") size: Int = 100,
    ): Response<MyStoreResponse>

    // 사용자
    @DELETE("/api/v2/signout")
    suspend fun signOut(): Response<BaseResponse<String>>

    @POST("/api/v2/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignResponse>

    @POST("/api/v2/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<BaseResponse<SignUser>>

    @POST("/api/v2/logout")
    suspend fun logout(): Response<BaseResponse<String>>

    @GET("/api/v2/user/me")
    suspend fun getMyInfo(): Response<MyInfoResponse>

    @PUT("/api/v2/user/me")
    suspend fun editNickname(@Body editNameRequest: EditNameRequest): Response<MyInfoResponse>

    @GET("/api/v2/user/name/check")
    suspend fun checkNickname(@Query("name") name: String): Response<BaseResponse<String>>

    // faq
    @GET("/api/v2/faq/categories")
    suspend fun getFAQCategory(): Response<FAQCategoryResponse>

    @GET("/api/v2/faqs")
    suspend fun getFAQByCategory(@Query("category") category: String): Response<FAQByCategoryResponse>
}