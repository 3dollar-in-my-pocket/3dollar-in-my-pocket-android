package com.zion830.threedollars.network

import com.zion830.threedollars.repository.model.request.NewReview
import com.zion830.threedollars.repository.model.request.NewUser
import com.zion830.threedollars.repository.model.response.*
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ServiceApi {

    // 카테고리
    @GET("/api/v1/category/distance")
    fun getCategoryByDistance(
        @Query("category") category: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<SearchByDistanceResponse>

    @GET("/api/v1/category/review")
    fun getCategoryByReview(
        @Query("category") category: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<SearchByReviewResponse>

    // 리뷰
    @GET("/api/v1/review/user")
    fun getReview(
        @Query("page") page: Int,
        @Query("userId") userId: Int
    )

    @POST("/api/v1/review/save")
    fun saveReview(
        @Query("storeId") storeId: Int,
        @Query("userId") userId: Int,
        @Body newReview: NewReview
    ): Call<ResponseBody>

    @PUT("/api/v1/review/{reviewId}")
    fun addReview(
        @Query("storeId") storeId: Int,
        @Query("userId") userId: Int
    ): Call<ResponseBody>

    @DELETE("/api/v1/review/{reviewId}")
    fun deleteReview(
        @Query("storeId") storeId: Int,
        @Query("userId") userId: Int
    ): Call<ResponseBody>

    // 가게
    @DELETE("/api/v1/store/delete")
    fun deleteStore(
        @Query("deleteReasonType") deleteReasonType: String, // TODO : enum으로 교체
        @Query("storeId") storeId: Int,
        @Query("userId") userId: Int
    ): Call<ResponseBody>

    @GET("/api/v1/store/detail")
    fun getStoreDetail(
        @Query("storeId") storeId: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<StoreDetailResponse>

    @GET("/api/v1/store/get")
    fun getAllStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<AllStoreResponse>

    @POST("/api/v1/store/save")
    @Multipart
    fun saveStore(
        @QueryMap storeInfo: Map<String, String>,
        @Part images: List<MultipartBody.Part>
    ): Call<AddStoreResponse>

    @POST("/api/v1/store/save")
    fun saveStore(
        @QueryMap storeInfo: Map<String, String>
    ): Call<AddStoreResponse>

    @PUT("/api/v1/store/update")
    @Multipart
    fun updateStore(
        @QueryMap storeInfo: Map<String, String>,
        @Part images: List<MultipartBody.Part>
    ): Call<ResponseBody>

    @PUT("/api/v1/store/update")
    fun updateStore(
        @QueryMap storeInfo: Map<String, String>
    ): Call<ResponseBody>

    @GET("/api/v1/store/user")
    fun getMyStore(
        @Query("userId") userId: Int,
        @Query("page") page: Int = 1
    ): Call<MyStoreResponse>

    @POST("/api/v1/store/{storeId}/images")
    fun saveImage(
        @Query("storeId") storeId: Int,
        @Part images: MultipartBody.Part
    ): Call<AddImageResponse>

    @GET("/api/v1/review/user")
    fun getMyReview(
        @Query("userId") userId: Int,
        @Query("page") page: Int = 1
    ): Call<MyReviewResponse>

    // 사용자
    @GET("/api/v1/user/info")
    suspend fun getUser(@Query("userId") userId: Int): UserInfoResponse?

    @POST("/api/v1/user/login")
    suspend fun tryLogin(@Body newUser: NewUser): LoginResponse?

    @PUT("/api/v1/user/nickname")
    suspend fun setName(
        @Query("nickName") name: String,
        @Query("userId") userId: Int
    ): Call<Response>
}