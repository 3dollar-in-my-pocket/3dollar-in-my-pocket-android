package com.home.domain.repository

import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.*
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

    fun getBossStoreDetail(bossStoreId: String, deviceLatitude: Double, deviceLongitude: Double): Flow<BaseResponse<BossStoreDetailModel>>
    fun getUserStoreDetail(
        storeId: Int,
        deviceLatitude: Double,
        deviceLongitude: Double,
        storeImagesCount: Int?,
        reviewsCount: Int?,
        visitHistoriesCount: Int?,
        filterVisitStartDate: String,
    ): Flow<BaseResponse<UserStoreDetailModel>>

    fun getMyInfo(): Flow<BaseResponse<UserModel>>

    fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>>

    fun postPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementModel>>>

    fun putFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>>

    fun deleteFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>>

    fun getFeedbackFull(targetType: String, targetId: String): Flow<BaseResponse<List<FoodTruckReviewModel>>>

    fun postFeedback(targetType: String, targetId: String, postFeedbackRequest: List<String>): Flow<BaseResponse<String>>

    fun deleteStore(storeId: Int, deleteReasonType: String): Flow<BaseResponse<DeleteResultModel>>

    fun postStoreVisit(storeId: Int, visitType: String): Flow<BaseResponse<String>>
}