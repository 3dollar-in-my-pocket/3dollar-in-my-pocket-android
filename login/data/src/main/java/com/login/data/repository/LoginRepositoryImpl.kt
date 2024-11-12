package com.login.data.repository

import com.login.data.datasource.LoginRemoteDataSource
import com.login.domain.data.AccessCheckModel
import com.login.domain.repository.LoginRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val loginRemoteDataSource: LoginRemoteDataSource) : LoginRepository {
    override fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>> =
        loginRemoteDataSource.getFeedbackTypes(targetType)

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
}