package com.threedollar.data.screen.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import com.threedollar.network.data.screen.StoreScreenResponse
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScreenRemoteDataSourceImpl @Inject constructor(
    private val serverApi: ServerApi,
) : ScreenRemoteDataSource {
    override fun getHomeListSection(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
        dynamicParams: Map<String, String>,
        cursor: String?,
    ): Flow<BaseResponse<HomeListSectionResponse>> = flow {
        emit(
            apiResult(
                serverApi.getHomeListSection(
                    distanceM = distanceM,
                    categoryIds = categoryIds,
                    targetStores = targetStores,
                    mapLatitude = mapLatitude,
                    mapLongitude = mapLongitude,
                    deviceLatitude = deviceLatitude,
                    deviceLongitude = deviceLongitude,
                    dynamicParams = dynamicParams,
                    cursor = cursor,
                )
            )
        )
    }

    override fun getStoreScreen(
        storeId: Long,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Flow<BaseResponse<StoreScreenResponse>> = flow {
        emit(apiResult(serverApi.getStoreScreen(storeId, deviceLatitude, deviceLongitude)))
    }

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
