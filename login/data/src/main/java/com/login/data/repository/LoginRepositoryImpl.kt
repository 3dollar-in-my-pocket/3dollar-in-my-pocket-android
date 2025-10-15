package com.login.data.repository

import com.login.data.datasource.LoginDataSource
import com.login.data.datasource.LoginRemoteDataSource
import com.login.domain.data.AccessCheckModel
import com.login.domain.repository.LoginRepository
import com.threedollar.common.base.BaseResponse
import com.login.domain.model.FeedbackTypeModel
import com.login.data.mapper.FeedbackTypeMapper
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignResponse
import com.threedollar.network.data.auth.SignUser
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val loginDataSource: LoginDataSource
) : LoginRepository {
    override fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeModel>>> =
        loginRemoteDataSource.getFeedbackTypes(targetType).map { response ->
            BaseResponse(
                ok = response.ok,
                data = response.data?.let { FeedbackTypeMapper.toDomainModelList(it) },
                message = response.message,
                resultCode = response.resultCode,
                error = response.error
            )
        }

    override fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>> =
        loginRemoteDataSource.putMarketingConsent(marketingConsent)

    override fun putPushInformation(pushToken: String): Flow<BaseResponse<String>> = loginRemoteDataSource.putPushInformation(pushToken)

    override fun getUserInfo(): Flow<AccessCheckModel> = loginRemoteDataSource.getUserInfo().map {
        AccessCheckModel(
            ok = it.ok,
            message = it.message,
            resultCode = it.resultCode
        )
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): SignResponse {
        val response = loginDataSource.signUp(signUpRequest)
        return response.body() ?: throw Exception("Sign up failed")
    }

    override suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>> {
        return loginDataSource.login(loginRequest)
    }

    override suspend fun logout(): Response<BaseResponse<String>> {
        return loginDataSource.logout()
    }

    override suspend fun signOut(): Response<BaseResponse<String>> {
        return loginDataSource.signOut()
    }

    override suspend fun putPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>> {
        return loginDataSource.putPushInformation(informationRequest)
    }
}