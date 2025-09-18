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

    override suspend fun login(loginRequest: LoginRequest): BaseResponse<SignUser> {
        val response = loginDataSource.login(loginRequest)
        return response.body() ?: throw Exception("Login failed")
    }

    override suspend fun logout(): BaseResponse<String> {
        val response = loginDataSource.logout()
        return response.body() ?: throw Exception("Logout failed")
    }

    override suspend fun signOut(): BaseResponse<String> {
        val response = loginDataSource.signOut()
        return response.body() ?: throw Exception("Sign out failed")
    }

    override suspend fun putPushInformation(informationRequest: PushInformationRequest): BaseResponse<String> {
        val response = loginDataSource.putPushInformation(informationRequest)
        return response.body() ?: throw Exception("Put push information failed")
    }
}