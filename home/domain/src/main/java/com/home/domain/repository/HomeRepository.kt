package com.home.domain.repository

import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.AroundStoreModel
import com.home.domain.data.user.UserModel
import com.threedollar.common.base.BaseResponse
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getAroundStores(
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<AroundStoreModel>>

    fun getMyInfo(): Flow<BaseResponse<UserModel>>

    fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>>

    fun postPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementModel>>>
}