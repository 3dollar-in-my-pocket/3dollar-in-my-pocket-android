package com.login.data.repository

import com.login.data.datasource.LoginRemoteDataSource
import com.login.domain.repository.LoginRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val loginRemoteDataSource: LoginRemoteDataSource) : LoginRepository {
    override fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>> = loginRemoteDataSource.getFeedbackTypes(targetType)
}