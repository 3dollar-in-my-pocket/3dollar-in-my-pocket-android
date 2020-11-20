package com.zion830.threedollars.network

import com.zion830.threedollars.repository.model.request.NewReview
import com.zion830.threedollars.repository.model.request.NewUser
import com.zion830.threedollars.repository.model.response.LoginResponse
import com.zion830.threedollars.repository.model.response.MyReviewResponse
import com.zion830.threedollars.repository.model.response.MyStoreResponse
import com.zion830.threedollars.repository.model.response.UserInfoResponse
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
    )

    @GET("/api/v1/category/review")
    fun getCategoryByReview(
        @Query("category") category: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    )

    // 리뷰
    @GET("/api/v1/review/save")
    fun getReview(
        @Query("page") page: Int,
        @Query("userId") userId: Int
    )

    @POST("/api/v1/review/review")
    fun saveReview(
        @Query("storeId") storeId: Int,
        @Query("userId") userId: Int
    )

    // 가게
    @DELETE("/api/v1/store/delete")
    fun deleteStore(
        @Query("deleteReasonType") deleteReasonType: String, // TODO : enum으로 교체
        @Query("storeId") storeId: Int,
        @Query("userId") userId: Int
    )

    @GET("/api/v1/store/detail")
    fun getStoreDetail(
        @Query("storeId") storeId: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    )

    @GET("/api/v1/store/get")
    fun getAllStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    )

    @POST("/api/v1/store/save")
    fun saveStore()

    @PUT("/api/v1/store/update")
    fun updateStore()

    @GET("/api/v1/store/user")
    fun getMyStore(
        @Query("userId") userId: Int,
        @Query("page") page: Int = 1
    ): Call<MyStoreResponse>

    // 리뷰
    @POST("/api/v1/review/save")
    fun saveNewReview(
        @Query("storeId") storeId: Int,
        @Query("userId") userId: Int,
        @Body newReview: NewReview
    ): ResponseBody

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