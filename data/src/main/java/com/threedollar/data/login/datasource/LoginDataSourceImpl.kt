package com.threedollar.data.login.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.api.LoginApi
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.LogoutDeviceRequest
import com.threedollar.network.data.auth.LogoutRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.request.PushInformationRequest
import retrofit2.Response
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val prefUtils: SharedPrefUtils
) : LoginDataSource {

    override suspend fun signUp(signUpRequest: SignUpRequest): Response<BaseResponse<SignUser>> {
        return loginApi.signUp(signUpRequest)
    }

    override suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>> {
        return loginApi.login(loginRequest)
    }

    override suspend fun logout(): Response<BaseResponse<String>> {
        return loginApi.logout(
            request = LogoutRequest(
                logoutDevice = LogoutDeviceRequest(
                    pushToken = prefUtils.getPushToken()
                )
            )
        )
    }

    override suspend fun signOut(): Response<BaseResponse<String>> {
        return loginApi.signOut()
    }

    override suspend fun putPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>> {
        return loginApi.putPushInformation(informationRequest).also {
            if (it.isSuccessful) {
                prefUtils.savePushToken(informationRequest.pushToken)
            }
        }
    }
}