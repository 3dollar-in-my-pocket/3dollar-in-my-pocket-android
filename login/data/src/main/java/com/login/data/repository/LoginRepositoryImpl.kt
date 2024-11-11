package com.login.data.repository

import com.login.data.datasource.LoginRemoteDataSource
import com.login.domain.repository.LoginRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val loginRemoteDataSource: LoginRemoteDataSource) : LoginRepository {
    override fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>> =
        loginRemoteDataSource.getFeedbackTypes(targetType)

    override fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>> =
        loginRemoteDataSource.putMarketingConsent(marketingConsent)

    override fun postPushInformation(pushToken: String): Flow<BaseResponse<String>> = loginRemoteDataSource.postPushInformation(pushToken)

    override fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>> = loginRemoteDataSource.getUserInfo()
}