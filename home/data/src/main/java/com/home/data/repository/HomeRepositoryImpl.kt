package com.home.data.repository

import com.home.data.asModel
import com.home.data.datasource.HomeRemoteDataSource
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.AroundStoreModel
import com.home.domain.data.user.UserModel
import com.home.domain.repository.HomeRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val homeRemoteDataSource: HomeRemoteDataSource) : HomeRepository {
    override fun getAroundStores(
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double
    ): Flow<BaseResponse<AroundStoreModel>> = homeRemoteDataSource.getAroundStores(
        categoryIds = categoryIds,
        targetStores = targetStores,
        sortType = sortType,
        filterCertifiedStores = filterCertifiedStores,
        mapLatitude = mapLatitude,
        mapLongitude = mapLongitude,
        deviceLatitude = deviceLatitude,
        deviceLongitude = deviceLongitude
    ).map {
        BaseResponse(
            data = it.data.asModel(),
            message = it.message,
            resultCode = it.resultCode
        )
    }

    override fun getMyInfo(): Flow<BaseResponse<UserModel>> = homeRemoteDataSource.getMyInfo().map {
        BaseResponse(
            data = it.data.asModel(),
            message = it.message,
            resultCode = it.resultCode
        )
    }

    override fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.putMarketingConsent(MarketingConsentRequest(marketingConsent))

    override fun postPushInformation(pushToken: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postPushInformation(PushInformationRequest(pushToken = pushToken))

    override fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementModel>>> =
        homeRemoteDataSource.getAdvertisements(position).map {
            BaseResponse(
                data = it.data.map {response -> response.asModel() },
                message = it.message,
                resultCode = it.resultCode
            )
        }
}