package com.threedollar.data.screen.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScreenRemoteDataSourceImpl @Inject constructor(
    private val serverApi: ServerApi,
) : ScreenRemoteDataSource {
    override fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<StoreContributorScreenResponse>> = flow {
        emit(apiResult(serverApi.getStoreContributorScreen(storeId)))
    }

    override fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<StoreContributorHistoriesResponse>> = flow {
        emit(apiResult(serverApi.getStoreContributorHistories(storeId, cursor)))
    }

    override fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenResponse>> = flow {
        emit(apiResult(serverApi.getHomeFilterScreen()))
    }
}
