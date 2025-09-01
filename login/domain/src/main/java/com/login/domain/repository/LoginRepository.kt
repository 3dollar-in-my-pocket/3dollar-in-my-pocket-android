package com.login.domain.repository

import com.login.domain.data.AccessCheckModel
import com.threedollar.common.base.BaseResponse
import com.login.domain.model.FeedbackTypeModel
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeModel>>>

    fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>>

    fun putPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getUserInfo(): Flow<AccessCheckModel>
}