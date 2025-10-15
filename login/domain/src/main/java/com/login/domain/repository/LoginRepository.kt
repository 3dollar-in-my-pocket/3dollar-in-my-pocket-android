package com.login.domain.repository

import com.login.domain.data.AccessCheckModel
import com.threedollar.common.base.BaseResponse
import com.login.domain.model.FeedbackTypeModel
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignResponse
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface LoginRepository {
    fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeModel>>>

    fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>>

    fun putPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getUserInfo(): Flow<AccessCheckModel>

    suspend fun signUp(signUpRequest: SignUpRequest): SignResponse

    suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>>

    suspend fun logout(): Response<BaseResponse<String>>

    suspend fun signOut(): Response<BaseResponse<String>>

    suspend fun putPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>>
}