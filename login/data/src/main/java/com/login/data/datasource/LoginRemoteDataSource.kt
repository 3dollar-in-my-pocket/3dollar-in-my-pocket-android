package com.login.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import kotlinx.coroutines.flow.Flow

interface LoginRemoteDataSource {

    fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>>

    fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>>

    fun putPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>>
}