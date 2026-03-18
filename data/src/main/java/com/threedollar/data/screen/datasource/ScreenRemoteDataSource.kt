package com.threedollar.data.screen.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import kotlinx.coroutines.flow.Flow

interface ScreenRemoteDataSource {
    fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<StoreContributorScreenResponse>>

    fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<StoreContributorHistoriesResponse>>
}
