package com.login.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import kotlinx.coroutines.flow.Flow

interface LoginRemoteDataSource {

    fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>>

}