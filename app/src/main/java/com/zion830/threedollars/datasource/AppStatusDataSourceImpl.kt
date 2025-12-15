package com.zion830.threedollars.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.util.apiResult
import com.threedollar.network.data.AppStatusResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppStatusDataSourceImpl @Inject constructor(private val serverApi: ServerApi) : AppStatusDataSource {
    override fun getAppStatus(): Flow<BaseResponse<AppStatusResponse>> = flow {
        emit(apiResult(serverApi.getAppStatus()))
    }
}