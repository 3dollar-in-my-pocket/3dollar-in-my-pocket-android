package com.home.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.home.data.asModel
import com.home.data.asRequest
import com.home.data.asType
import com.home.data.datasource.HomeRemoteDataSource
import com.home.data.datasource.ImagePagingDataSource
import com.home.data.datasource.PlacePagingDataSource
import com.home.data.datasource.ReviewPagingDataSource
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.data.place.PlaceModel
import com.home.domain.data.store.AroundStoreModel
import com.home.domain.data.store.BossStoreDetailModel
import com.home.domain.data.store.DeleteResultModel
import com.home.domain.data.store.EditStoreReviewModel
import com.home.domain.data.store.FoodTruckReviewModel
import com.home.domain.data.store.ImageContentModel
import com.home.domain.data.store.PostUserStoreModel
import com.home.domain.data.store.ReportReasonsModel
import com.home.domain.data.store.ReviewContentModel
import com.home.domain.data.store.ReviewSortType
import com.home.domain.data.store.SaveImagesModel
import com.home.domain.data.store.StoreNearExistsModel
import com.home.domain.data.store.UserStoreDetailModel
import com.home.domain.data.user.UserModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.FilterConditionsTypeModel
import com.home.domain.request.PlaceRequest
import com.home.domain.request.PlaceType
import com.home.domain.request.ReportReasonsGroupType
import com.home.domain.request.ReportReviewModelRequest
import com.home.domain.request.UserStoreModelRequest
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.common.utils.SharedPrefUtils.Companion.BOSS_FEED_BACK_LIST
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PostFeedbackRequest
import com.threedollar.network.request.PostStoreVisitRequest
import com.threedollar.network.request.PushInformationRequest
import com.threedollar.network.request.StoreReviewRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val homeRemoteDataSource: HomeRemoteDataSource,
    private val sharedPrefUtils: SharedPrefUtils,
) :
    HomeRepository {
    override fun getAroundStores(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        filterConditionsTypeModel: List<FilterConditionsTypeModel>,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<AroundStoreModel>> = homeRemoteDataSource.getAroundStores(
        distanceM = distanceM,
        categoryIds = categoryIds,
        targetStores = targetStores,
        sortType = sortType,
        filterCertifiedStores = filterCertifiedStores,
        filterConditionsType = filterConditionsTypeModel.map { it.asType() },
        mapLatitude = mapLatitude,
        mapLongitude = mapLongitude,
        deviceLatitude = deviceLatitude,
        deviceLongitude = deviceLongitude,
    ).map {
        BaseResponse(
            ok = it.ok,
            data = it.data?.asModel(),
            message = it.message,
            resultCode = it.resultCode,
            error = it.error,
        )
    }

    override fun getBossStoreDetail(bossStoreId: String, deviceLatitude: Double, deviceLongitude: Double): Flow<BaseResponse<BossStoreDetailModel>> =
        homeRemoteDataSource.getBossStoreDetail(bossStoreId, deviceLatitude, deviceLongitude).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
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
            filterVisitStartDate = filterVisitStartDate,
        ).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun getMyInfo(): Flow<BaseResponse<UserModel>> = homeRemoteDataSource.getMyInfo().map {
        BaseResponse(
            ok = it.ok,
            data = it.data?.asModel(),
            message = it.message,
            resultCode = it.resultCode,
            error = it.error,
        )
    }

    override fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.putMarketingConsent(MarketingConsentRequest(marketingConsent))

    override fun putPushInformation(pushToken: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.putPushInformation(PushInformationRequest(pushToken = pushToken))

    override fun getAdvertisements(
        position: AdvertisementsPosition,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<List<AdvertisementModelV2>>> =
        homeRemoteDataSource.getAdvertisements(
            position = position,
            deviceLatitude = deviceLatitude,
            deviceLongitude = deviceLongitude
        ).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.advertisements?.map { response -> response.asModel() }.orEmpty(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun putFavorite(storeId: String): Flow<BaseResponse<String>> = homeRemoteDataSource.putFavorite(storeId)

    override fun deleteFavorite(storeId: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.deleteFavorite(storeId)

    override fun getFeedbackFull(targetType: String, targetId: String): Flow<BaseResponse<List<FoodTruckReviewModel>>> {
        val feedbackTypeResponseList = sharedPrefUtils.getList<FeedbackTypeResponse>(BOSS_FEED_BACK_LIST)
        return homeRemoteDataSource.getFeedbackFull(targetType, targetId).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.map { feedbackCountResponse -> feedbackCountResponse.asModel(feedbackTypeResponseList) },
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }
    }

    override fun postFeedback(targetType: String, targetId: String, postFeedbackRequest: List<String>): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postFeedback(
            targetType = targetType,
            targetId = targetId,
            postFeedbackRequest = PostFeedbackRequest(postFeedbackRequest),
        )

    override fun deleteStore(storeId: Int, deleteReasonType: String): Flow<BaseResponse<DeleteResultModel>> =
        homeRemoteDataSource.deleteStore(storeId, deleteReasonType).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun postStoreVisit(storeId: Int, visitType: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postStoreVisit(PostStoreVisitRequest(storeId = storeId, type = visitType))

    override fun deleteImage(imageId: Int): Flow<BaseResponse<String>> = homeRemoteDataSource.deleteImage(imageId)

    override suspend fun saveImages(images: List<MultipartBody.Part>, storeId: Int): BaseResponse<List<SaveImagesModel>>? = runCatching {
        homeRemoteDataSource.saveImages(images, storeId)
    }.fold(
        onSuccess = {
            BaseResponse(
                ok = it.ok,
                data = it.data?.map { response -> response.asModel() },
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        },
        onFailure = {
            null
        }
    )


    override fun getStoreImages(storeId: Int): Flow<PagingData<ImageContentModel>> = Pager(PagingConfig(20)) {
        ImagePagingDataSource(storeId, serverApi)
    }.flow

    override fun postStoreReview(contents: String, rating: Int?, storeId: Int) =
        homeRemoteDataSource.postStoreReview(StoreReviewRequest(contents, rating, storeId)).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun putStoreReview(reviewId: Int, contents: String, rating: Int): Flow<BaseResponse<EditStoreReviewModel>> =
        homeRemoteDataSource.putStoreReview(reviewId, StoreReviewRequest(contents = contents, rating = rating)).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun getStoreReview(storeId: Int, reviewSortType: ReviewSortType): Flow<PagingData<ReviewContentModel>> = Pager(PagingConfig(20)) {
        ReviewPagingDataSource(storeId = storeId, sort = reviewSortType.name, serverApi = serverApi)
    }.flow

    override fun getStoreNearExists(distance: Double, mapLatitude: Double, mapLongitude: Double): Flow<BaseResponse<StoreNearExistsModel>> =
        homeRemoteDataSource.getStoreNearExists(distance, mapLatitude, mapLongitude).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun postUserStore(userStoreModelRequest: UserStoreModelRequest): Flow<BaseResponse<PostUserStoreModel>> =
        homeRemoteDataSource.postUserStore(userStoreModelRequest.asRequest()).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun putUserStore(userStoreModelRequest: UserStoreModelRequest, storeId: Int): Flow<BaseResponse<PostUserStoreModel>> =
        homeRemoteDataSource.putUserStore(userStoreRequest = userStoreModelRequest.asRequest(), storeId = storeId).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun reportStoreReview(storeId: Int, reviewId: Int, reportReviewModelRequest: ReportReviewModelRequest): Flow<BaseResponse<String>> =
        homeRemoteDataSource.reportStoreReview(storeId, reviewId, reportReviewModelRequest.asRequest())

    override fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsModel>> =
        homeRemoteDataSource.getReportReasons(reportReasonsGroupType).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun postPlace(placeRequest: PlaceRequest, placeType: PlaceType): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postPlace(placeRequest = placeRequest.asRequest(), placeType = placeType.asType())

    override fun deletePlace(placeType: PlaceType, placeId: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.deletePlace(placeType = placeType.asType(), placeId = placeId)

    override fun getPlace(placeType: PlaceType): Flow<PagingData<PlaceModel>> = Pager(PagingConfig(20)) {
        PlacePagingDataSource(placeType = placeType.asType(), serverApi = serverApi)
    }.flow
}
