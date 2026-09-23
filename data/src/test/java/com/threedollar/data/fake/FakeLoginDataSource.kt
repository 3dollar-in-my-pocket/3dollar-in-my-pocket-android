package com.threedollar.data.fake

import com.threedollar.common.base.BaseResponse
import com.threedollar.data.login.datasource.LoginDataSource
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.request.PushInformationRequest
import retrofit2.Response

/**
 * [LoginDataSource] 테스트 대역. 스텁하지 않은 메서드를 부르면 어떤 메서드인지 담아 실패한다.
 * 마지막으로 받은 요청을 `last*Request` 로 기록한다.
 */
class FakeLoginDataSource(
    private val loginResult: () -> Response<BaseResponse<SignUser>> = { error("login 스텁 없음") },
    private val signUpResult: () -> Response<BaseResponse<SignUser>> = { error("signUp 스텁 없음") },
    private val putPushInformationResult: () -> Response<BaseResponse<String>> = { error("putPushInformation 스텁 없음") },
) : LoginDataSource {
    var lastLoginRequest: LoginRequest? = null
    var lastSignUpRequest: SignUpRequest? = null
    var lastPushInformationRequest: PushInformationRequest? = null

    override suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>> {
        lastLoginRequest = loginRequest
        return loginResult()
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): Response<BaseResponse<SignUser>> {
        lastSignUpRequest = signUpRequest
        return signUpResult()
    }

    override suspend fun putPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>> {
        lastPushInformationRequest = informationRequest
        return putPushInformationResult()
    }

    override suspend fun logout(): Response<BaseResponse<String>> = error("logout 스텁 없음")

    override suspend fun signOut(): Response<BaseResponse<String>> = error("signOut 스텁 없음")
}
