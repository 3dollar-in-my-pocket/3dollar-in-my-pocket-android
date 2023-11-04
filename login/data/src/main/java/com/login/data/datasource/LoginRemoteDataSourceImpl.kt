package com.login.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(private val serverApi: ServerApi) : LoginRemoteDataSource {

    override fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>> = flow {
        emit(apiResult(serverApi.getFeedbackTypes(targetType)))
    }
}