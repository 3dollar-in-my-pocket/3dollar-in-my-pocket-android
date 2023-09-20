package com.home.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow

interface HomeRemoteDataSource {

    fun getAroundStores(
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<AroundStoreResponse>>

    fun getMyInfo(): Flow<BaseResponse<UserResponse>>

    fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest): Flow<BaseResponse<String>>

    fun postPushInformation(informationRequest: PushInformationRequest): Flow<BaseResponse<String>>

    fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementResponse>>>
}