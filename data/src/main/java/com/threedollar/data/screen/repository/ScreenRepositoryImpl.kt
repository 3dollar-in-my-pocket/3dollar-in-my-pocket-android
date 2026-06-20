package com.threedollar.data.screen.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.HomeFilterScreenModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.data.screen.asCardsSectionModel
import com.threedollar.data.screen.asModel
import com.threedollar.data.screen.datasource.ScreenRemoteDataSource
import com.threedollar.domain.screen.repository.ScreenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScreenRepositoryImpl @Inject constructor(
    private val screenRemoteDataSource: ScreenRemoteDataSource,
) : ScreenRepository {
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
    ): Flow<BaseResponse<HomeListSectionModel>> =
        screenRemoteDataSource.getHomeListSection(
            distanceM = distanceM,
            categoryIds = categoryIds,
            targetStores = targetStores,
            mapLatitude = mapLatitude,
            mapLongitude = mapLongitude,
            deviceLatitude = deviceLatitude,
            deviceLongitude = deviceLongitude,
            dynamicParams = dynamicParams,
            cursor = cursor,
        ).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel() ?: HomeListSectionModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<SDScreenModel>> =
        screenRemoteDataSource.getStoreContributorScreen(storeId).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel() ?: SDScreenModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<SDSectionModel.CardsSection>> =
        screenRemoteDataSource.getStoreContributorHistories(storeId, cursor).map {
            val section = it.data?.asCardsSectionModel() ?: SDSectionModel.CardsSection(type = "")
            BaseResponse(
                ok = it.ok,
                data = section,
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenModel>> =
        screenRemoteDataSource.getHomeFilterScreen().map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel() ?: HomeFilterScreenModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }
}
