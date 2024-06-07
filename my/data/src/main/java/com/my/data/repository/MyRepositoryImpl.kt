package com.my.data.repository

import com.my.domain.repository.MyRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(private val serverApi: ServerApi) : MyRepository {

    override fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>> = flow {
        emit(apiResult(serverApi.getUserInfo()))
    }

}