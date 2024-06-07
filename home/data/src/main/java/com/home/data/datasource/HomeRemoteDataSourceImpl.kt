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
import com.threedollar.network.data.store.ReviewContent
import com.threedollar.network.data.store.SaveImagesResponse
import com.threedollar.network.data.store.StoreNearExistResponse
import com.threedollar.network.data.store.UserStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.request.MarketingConsentRequest
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
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<AroundStoreResponse>> = flow {
        emit(
            apiResult(
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

    override fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>> = flow {
        emit(apiResult(serverApi.getUserInfo()))
    }

    override fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.putMarketingConsent(marketingConsentRequest)))
    }

    override fun postPushInformation(informationRequest: PushInformationRequest): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.postPushInformation(informationRequest)))
    }

    override fun getAdvertisements(position: AdvertisementsPosition): Flow<BaseResponse<AdvertisementResponse>> = flow {
        emit(apiResult(serverApi.getAdvertisements(position.name)))
    }

    override fun putFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.putFavorite(storeType, storeId)))
    }

    override fun deleteFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>> = flow {
        emit(apiResult(serverApi.deleteFavorite(storeType, storeId)))
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

    override fun saveImages(images: List<MultipartBody.Part>, storeId: Int): Flow<BaseResponse<List<SaveImagesResponse>>> = flow {
        emit(apiResult(serverApi.saveImages(images, storeId)))
    }

    override fun postStoreReview(storeReviewRequest: StoreReviewRequest): Flow<BaseResponse<ReviewContent>> = flow {
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
}