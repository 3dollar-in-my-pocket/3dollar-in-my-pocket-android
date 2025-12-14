package com.zion830.threedollars.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.AppStatusResponse
import kotlinx.coroutines.flow.Flow

interface AppStatusDataSource {
    fun getAppStatus(): Flow<BaseResponse<AppStatusResponse>>
}