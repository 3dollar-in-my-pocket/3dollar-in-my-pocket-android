package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.LogoutRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.data.user.UserRandomNameResponse
import com.threedollar.network.request.PushInformationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface LoginApi {

    @DELETE("/api/v2/signout")
    suspend fun signOut(): Response<BaseResponse<String>>

    @POST("/api/v2/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<BaseResponse<SignUser>>

    @POST("/api/v2/signup/random-name")
    suspend fun signUpWithRandomName(@Body signUpRequest: SignUpRequest): Response<BaseResponse<SignUser>>

    @POST("/api/v2/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<BaseResponse<SignUser>>

    @POST("/api/v2/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): Response<BaseResponse<String>>

    @PUT("/api/v2/device")
    suspend fun putPushInformation(@Body informationRequest: PushInformationRequest): Response<BaseResponse<String>>

    @POST("/api/v1/user/random-names")
    suspend fun postRandomName(): Response<BaseResponse<UserRandomNameResponse>>

    @GET("/api/v2/user/name/check")
    suspend fun checkName(
        @Query("name") name: String
    ): Response<BaseResponse<String>>
}