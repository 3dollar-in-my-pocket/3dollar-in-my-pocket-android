package com.home.data.datasource

import com.home.domain.request.ReportReasonsGroupType
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.store.BossStoreResponse
import com.threedollar.network.data.store.DeleteResultResponse
import com.threedollar.network.data.store.EditStoreReviewResponse
import com.threedollar.network.data.store.PostUserStoreResponse
import com.threedollar.network.data.store.StoreReviewDetailResponse
import com.threedollar.network.data.store.SaveImagesResponse
import com.threedollar.network.data.store.StoreNearExistResponse
import com.threedollar.network.data.store.UserStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.FilterConditionsType
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PlaceRequest
import com.threedollar.network.request.PlaceType
import com.threedollar.network.request.PostFeedbackRequest
import com.threedollar.network.request.PostStoreVisitRequest
import com.threedollar.network.request.PushInformationRequest
import com.threedollar.network.request.ReportReviewRequest
import com.threedollar.network.request.StoreReviewRequest
import com.threedollar.network.request.UserStoreRequest
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class HomeRemoteDataSourceImpl @Inject constructor(private val serverApi: ServerApi) : HomeRemoteDataSource {
    override fun getAroundStores(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        filterConditionsType: List<FilterConditionsType>,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<AroundStoreResponse>> = flow {
        emit(
            apiResult(
                serverApi.getAroundStores(
                    distanceM = if (distanceM.isNaN() || distanceM <= 0) 100000.0 else distanceM,
                    categoryIds = categoryIds,
                    targetStores = targetStores,
                    sortType = sortType,
                    filterCertifiedStores = filterCertifiedStores,
                    filterConditions = filterConditionsType.map { it.name },
                    mapLatitude = mapLatitude,
                    mapLongitude = mapLongitude,
                    deviceLatitude = deviceLatitude,
                    deviceLongitude = deviceLongitude,
                )
            )
        )
    }

    override fun getBossStoreDetail(bossStoreId: String, deviceLatitude: Double, deviceLongitude: Double): Flow<BaseResponse<BossStoreResponse>> =
        flow {
            emit(
                apiResult(
                    serverApi.getBossStoreDetail(
                        bossStoreId = bossStoreId,
                        deviceLatitude = deviceLatitude,
                        deviceLongitude = deviceLongitude
                    )
                )
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
    ): Flow<BaseResponse<UserStoreResponse>> = flow {
        emit(
            apiResult(
                serverApi.getUserStoreDetail(
                    storeId = storeId,
                    deviceLatitude = deviceLatitude,
                    deviceLongitude = deviceLongitude,
                    storeImagesCount = storeImagesCount,
                    reviewsCount = reviewsCount,
                    visitHistoriesCount = visitHistoriesCount,
                    filterVisitStartDate = filterVisitStartDate
                )
            )
        )
    }

    override fun getMyInfo(): Flow<BaseResponse<UserResponse>> = flow {
        emit(apiResult(serverApi.getMyInfo()))
    }

    override fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.putMarketingConsent(marketingConsentRequest)))
    }

    override fun putPushInformation(informationRequest: PushInformationRequest): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.putPushInformation(informationRequest)))
    }

    override fun getAdvertisements(
        position: AdvertisementsPosition,
        deviceLatitude: Double,
        deviceLongitude: Double
    ): Flow<BaseResponse<AdvertisementResponse>> = flow {
        emit(
            apiResult(
                serverApi.getAdvertisements(
                    position = position.name,
                    deviceLatitude = deviceLatitude,
                    deviceLongitude = deviceLongitude
                )
            )
        )
    }

    override fun putFavorite(storeId: String): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.putFavorite(storeId)))
    }

    override fun deleteFavorite(storeId: String): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.deleteFavorite(storeId)))
    }

    override fun getFeedbackFull(targetType: String, targetId: String): Flow<BaseResponse<List<FeedbackCountResponse>>> = flow {
        emit(apiResult(serverApi.getFeedbackFull(targetType, targetId)))
    }

    override fun postFeedback(targetType: String, targetId: String, postFeedbackRequest: PostFeedbackRequest): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.postFeedback(targetType, targetId, postFeedbackRequest)))
    }

    override fun deleteStore(storeId: Int, deleteReasonType: String): Flow<BaseResponse<DeleteResultResponse>> = flow {
        emit(apiResult(serverApi.deleteStore(storeId, deleteReasonType)))
    }

    override fun postStoreVisit(postStoreVisitRequest: PostStoreVisitRequest): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.postStoreVisit(postStoreVisitRequest)))
    }

    override fun deleteImage(imageId: Int): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.deleteImage(imageId)))
    }

    override suspend fun saveImages(images: List<MultipartBody.Part>, storeId: Int): BaseResponse<List<SaveImagesResponse>> = apiResult(serverApi.saveImages(images, storeId))

    override fun postStoreReview(storeReviewRequest: StoreReviewRequest): Flow<BaseResponse<StoreReviewDetailResponse>> = flow {
        emit(apiResult(serverApi.postStoreReview(storeReviewRequest)))
    }

    override fun putStoreReview(reviewId: Int, storeReviewRequest: StoreReviewRequest): Flow<BaseResponse<EditStoreReviewResponse>> = flow {
        emit(apiResult(serverApi.putStoreReview(reviewId, storeReviewRequest)))
    }

    override fun getStoreNearExists(distance: Double, mapLatitude: Double, mapLongitude: Double): Flow<BaseResponse<StoreNearExistResponse>> = flow {
        emit(apiResult(serverApi.getStoreNearExists(distance, mapLatitude, mapLongitude)))
    }

    override fun postUserStore(userStoreRequest: UserStoreRequest): Flow<BaseResponse<PostUserStoreResponse>> = flow {
        emit(apiResult(serverApi.postUserStore(userStoreRequest)))
    }

    override fun putUserStore(userStoreRequest: UserStoreRequest, storeId: Int): Flow<BaseResponse<PostUserStoreResponse>> = flow {
        emit(apiResult(serverApi.putUserStore(userStoreRequest = userStoreRequest, storeId = storeId)))
    }

    override fun reportStoreReview(storeId: Int, reviewId: Int, reportReviewRequest: ReportReviewRequest): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.reportStoreReview(storeId, reviewId, reportReviewRequest)))
    }

    override fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsResponse>> = flow {
        emit(apiResult(serverApi.getReportReasons(reportReasonsGroupType.name)))
    }

    override fun postPlace(placeRequest: PlaceRequest, placeType: PlaceType): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.postPlace(placeRequest = placeRequest, placeType = placeType.name)))
    }

    override fun deletePlace(placeType: PlaceType, placeId: String): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.deletePlace(placeType = placeType.name, placeId = placeId)))
    }

}