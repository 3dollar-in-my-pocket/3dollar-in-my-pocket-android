package com.threedollar.data.screen.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import kotlinx.coroutines.flow.Flow

interface ScreenRemoteDataSource {
    fun getHomeListSection(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
        dynamicParams: Map<String, String>,
        cursor: String?,
    ): Flow<BaseResponse<HomeListSectionResponse>>

    fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<StoreContributorScreenResponse>>

    fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<StoreContributorHistoriesResponse>>

    fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenResponse>>
}
