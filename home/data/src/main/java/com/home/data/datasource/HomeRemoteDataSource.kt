package com.home.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.store.BossStoreResponse
import com.threedollar.network.data.store.DeleteResultResponse
import com.threedollar.network.data.store.UserStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PostFeedbackRequest
import com.threedollar.network.request.PostStoreVisitRequest
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

    fun getBossStoreDetail(
        bossStoreId: String,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<BossStoreResponse>>

    fun getMyInfo(): Flow<BaseResponse<UserResponse>>

    fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest): Flow<BaseResponse<String>>

    fun postPushInformation(informationRequest: PushInformationRequest): Flow<BaseResponse<String>>

    fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementResponse>>>

    fun putFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>>

    fun deleteFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>>

    fun getFeedbackFull(targetType: String, targetId: String): Flow<BaseResponse<List<FeedbackCountResponse>>>

    fun postFeedback(targetType: String, targetId: String, postFeedbackRequest: PostFeedbackRequest): Flow<BaseResponse<String>>

    fun getUserStoreDetail(
        storeId: Int,
        deviceLatitude: Double,
        deviceLongitude: Double,
        storeImagesCount: Int?,
        reviewsCount: Int?,
        visitHistoriesCount: Int?,
        filterVisitStartDate: String,
    ): Flow<BaseResponse<UserStoreResponse>>

    fun deleteStore(
        storeId: Int,
        deleteReasonType: String
    ): Flow<BaseResponse<DeleteResultResponse>>

    fun postStoreVisit(postStoreVisitRequest: PostStoreVisitRequest): Flow<BaseResponse<String>>
}