package com.home.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRemoteDataSourceImpl @Inject constructor(private val serverApi: ServerApi) : HomeRemoteDataSource {
    override fun getAroundStores(
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double
    ): Flow<BaseResponse<AroundStoreResponse>> = flow {
        emit(
            serverApi.getAroundStores(
                categoryIds = categoryIds,
                targetStores = targetStores,
                sortType = sortType,
                filterCertifiedStores = filterCertifiedStores,
                mapLatitude = mapLatitude,
                mapLongitude = mapLongitude,
                deviceLatitude = deviceLatitude,
                deviceLongitude = deviceLongitude,
            )
        )
    }

    override fun getMyInfo(): Flow<BaseResponse<UserResponse>> = flow {
        emit(serverApi.getMyInfo())
    }

    override fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest): Flow<BaseResponse<String>> = flow {
        emit(serverApi.putMarketingConsent(marketingConsentRequest))
    }

    override fun postPushInformation(informationRequest: PushInformationRequest): Flow<BaseResponse<String>> = flow {
        emit(serverApi.postPushInformation(informationRequest))
    }

    override fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementResponse>>> = flow {
        emit(serverApi.getAdvertisements(position))
    }

}