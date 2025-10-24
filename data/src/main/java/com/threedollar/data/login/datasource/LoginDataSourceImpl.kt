package com.threedollar.data.login.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.request.PushInformationRequest
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignResponse
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.api.LoginApi
import retrofit2.Response
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    private val loginApi: LoginApi
) : LoginDataSource {

    override suspend fun signUp(signUpRequest: SignUpRequest): Response<SignResponse> {
        return loginApi.signUp(signUpRequest)
    }

    override suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>> {
        return loginApi.login(loginRequest)
    }

    override suspend fun logout(): Response<BaseResponse<String>> {
        return loginApi.logout()
    }

    override suspend fun signOut(): Response<BaseResponse<String>> {
        return loginApi.signOut()
    }

    override suspend fun putPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>> {
        return loginApi.putPushInformation(informationRequest)
    }
}