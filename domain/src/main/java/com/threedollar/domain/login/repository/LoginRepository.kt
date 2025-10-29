package com.threedollar.domain.login.repository

import com.threedollar.domain.login.data.AccessCheckModel
import com.threedollar.common.base.BaseResponse
import com.threedollar.domain.login.model.FeedbackTypeModel
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface LoginRepository {
    fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeModel>>>

    fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>>

    fun putPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getUserInfo(): Flow<AccessCheckModel>

    suspend fun signUp(signUpRequest: SignUpRequest): Response<BaseResponse<SignUser>>

    suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>>

    suspend fun logout(): Response<BaseResponse<String>>

    suspend fun signOut(): Response<BaseResponse<String>>

    suspend fun putPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>>
}