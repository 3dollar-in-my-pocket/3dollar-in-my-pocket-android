package com.home.data.repository

import com.home.data.asModel
import com.home.data.datasource.HomeRemoteDataSource
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.AroundStoreModel
import com.home.domain.data.store.BossStoreDetailModel
import com.home.domain.data.store.FoodTruckReviewModel
import com.home.domain.data.store.UserStoreDetailModel
import com.home.domain.data.user.UserModel
import com.home.domain.repository.HomeRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.common.utils.SharedPrefUtils.Companion.BOSS_FEED_BACK_LIST
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PostFeedbackRequest
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val homeRemoteDataSource: HomeRemoteDataSource, private val sharedPrefUtils: SharedPrefUtils) :
    HomeRepository {
    override fun getAroundStores(
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
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

    override fun getBossStoreDetail(bossStoreId: String, deviceLatitude: Double, deviceLongitude: Double): Flow<BaseResponse<BossStoreDetailModel>> =
        homeRemoteDataSource.getBossStoreDetail(bossStoreId, deviceLatitude, deviceLongitude).map {
            BaseResponse(
                data = it.data.asModel(),
                message = it.message,
                resultCode = it.resultCode
            )
        }

    override fun getUserStoreDetail(
        storeId: Int,
        deviceLatitude: Double,
        deviceLongitude: Double,
        storeImagesCount: Int?,
        reviewsCount: Int?,
        visitHistoriesCount: Int?,
        filterVisitStartDate: String,
    ): Flow<BaseResponse<UserStoreDetailModel>> =
        homeRemoteDataSource.getUserStoreDetail(
            storeId = storeId,
            deviceLatitude = deviceLatitude,
            deviceLongitude = deviceLongitude,
            storeImagesCount = storeImagesCount,
            reviewsCount = reviewsCount,
            visitHistoriesCount = visitHistoriesCount,
            filterVisitStartDate = filterVisitStartDate
        ).map {
            BaseResponse(
                ok = it.ok,
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
                data = it.data.map { response -> response.asModel() },
                message = it.message,
                resultCode = it.resultCode
            )
        }

    override fun putFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>> = homeRemoteDataSource.putFavorite(storeType, storeId)

    override fun deleteFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.deleteFavorite(storeType, storeId)

    override fun getFeedbackFull(targetType: String, targetId: String): Flow<BaseResponse<List<FoodTruckReviewModel>>> {
        val feedbackTypeResponseList = sharedPrefUtils.getList<FeedbackTypeResponse>(BOSS_FEED_BACK_LIST)
        return homeRemoteDataSource.getFeedbackFull(targetType, targetId).map {
            BaseResponse(
                data = it.data.map { feedbackCountResponse -> feedbackCountResponse.asModel(feedbackTypeResponseList) },
                message = it.message,
                resultCode = it.resultCode
            )
        }
    }

    override fun postFeedback(targetType: String, targetId: String, postFeedbackRequest: List<String>): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postFeedback(
            targetType = targetType,
            targetId = targetId,
            postFeedbackRequest = PostFeedbackRequest(postFeedbackRequest)
        )
}