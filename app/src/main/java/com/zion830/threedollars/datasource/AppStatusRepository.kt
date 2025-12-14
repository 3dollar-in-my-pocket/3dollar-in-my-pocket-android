package com.zion830.threedollars.datasource

import com.threedollar.common.base.BaseResponse
import com.zion830.threedollars.datasource.model.AppUpdateDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AppStatusRepository {
    fun getMinimumVersion(): Flow<BaseResponse<AppUpdateDialog>>
}

class AppStatusRepositoryImpl @Inject constructor(private val dataSource: AppStatusDataSource): AppStatusRepository {
    override fun getMinimumVersion(): Flow<BaseResponse<AppUpdateDialog>> {
        return dataSource.getAppStatus().map {
            var appUpdateDialog: AppUpdateDialog? = null

            it.data?.let { response ->
                appUpdateDialog = AppUpdateDialog(
                    enabled = response.forceUpdate.enabled,
                    message = response.forceUpdate.message,
                    linkUrl = response.forceUpdate.linkUrl
                )
            }

            BaseResponse(
                it.ok,
                appUpdateDialog,
                message = it.message,
                resultCode = it.resultCode,
                it.error
            )
        }
    }
}