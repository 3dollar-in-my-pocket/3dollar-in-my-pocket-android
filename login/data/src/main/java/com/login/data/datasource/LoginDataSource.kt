package com.login.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.request.PushInformationRequest
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignResponse
import com.threedollar.network.data.auth.SignUser
import retrofit2.Response

interface LoginDataSource {
    suspend fun signUp(signUpRequest: SignUpRequest): Response<SignResponse>
    suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>>
    suspend fun logout(): Response<BaseResponse<String>>
    suspend fun signOut(): Response<BaseResponse<String>>
    suspend fun putPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>>
}