package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.request.PushInformationRequest
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignResponse
import com.threedollar.network.data.auth.SignUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT

interface LoginApi {

    @POST("/api/v1/user/signup")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequest
    ): Response<SignResponse>

    @POST("/api/v1/user/signin")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<BaseResponse<SignUser>>

    @DELETE("/api/v1/user/logout")
    suspend fun logout(): Response<BaseResponse<String>>

    @DELETE("/api/v1/user/signout")
    suspend fun signOut(): Response<BaseResponse<String>>

    @PUT("/api/v1/user/device/push-information")
    suspend fun putPushInformation(
        @Body pushInformationRequest: PushInformationRequest
    ): Response<BaseResponse<String>>
}