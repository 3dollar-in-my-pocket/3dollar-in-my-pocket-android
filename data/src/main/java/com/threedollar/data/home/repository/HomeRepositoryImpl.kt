package com.threedollar.data.home.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.threedollar.data.home.asModel
import com.threedollar.data.home.asRequest
import com.threedollar.data.home.asType
import com.threedollar.data.home.datasource.HomeRemoteDataSource
import com.threedollar.data.home.datasource.ImagePagingDataSource
import com.threedollar.data.home.datasource.PlacePagingDataSource
import com.threedollar.data.home.datasource.ReviewPagingDataSource
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.place.PlaceModel
import com.threedollar.domain.home.data.store.AroundStoreModel
import com.threedollar.domain.home.data.store.BossStoreDetailModel
import com.threedollar.domain.home.data.store.DeleteResultModel
import com.threedollar.domain.home.data.store.EditStoreReviewModel
import com.threedollar.domain.home.data.store.FeedbackExistsModel
import com.threedollar.domain.home.data.store.FoodTruckReviewModel
import com.threedollar.domain.home.data.store.ImageContentModel
import com.threedollar.domain.home.data.store.PostUserStoreModel
import com.threedollar.domain.home.data.store.ReportReasonsModel
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.domain.home.data.store.ReviewSortType
import com.threedollar.domain.home.data.store.SaveImagesModel
import com.threedollar.domain.home.data.store.StoreNearExistsModel
import com.threedollar.domain.home.data.store.UploadFileModel
import com.threedollar.domain.home.data.store.UserStoreDetailModel
import com.threedollar.domain.home.data.user.UserModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.threedollar.domain.home.request.PlaceRequest
import com.threedollar.domain.home.request.PlaceType
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.common.utils.SharedPrefUtils.Companion.BOSS_FEED_BACK_LIST
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.feedback.FeedbackExistsResponse
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

    override suspend fun uploadFilesBulk(fileType: String, files: List<MultipartBody.Part>): BaseResponse<List<UploadFileModel>>? = runCatching {
        homeRemoteDataSource.uploadFilesBulk(fileType, files)
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

    override suspend fun uploadFile(fileType: String, file: MultipartBody.Part): BaseResponse<UploadFileModel>? = runCatching {
        homeRemoteDataSource.uploadFile(fileType, file)
    }.fold(
        onSuccess = {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
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

    override fun putStickers(storeId: String, reviewId: String, stickers: List<String>): Flow<BaseResponse<String>>
        = homeRemoteDataSource.putStickers(storeId, reviewId, stickers)

    override fun postBossStoreReview(storeId: String, contents: String, rating: Int, images: List<UploadFileModel>, feedbacks: List<String>): Flow<BaseResponse<ReviewContentModel>> =
        homeRemoteDataSource.postBossStoreReview(storeId, contents, rating, images, feedbacks).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error,
            )
        }

    override fun checkFeedbackExists(targetType: String, targetId: String): Flow<BaseResponse<FeedbackExistsModel>> =
        homeRemoteDataSource.checkFeedbackExists(targetType, targetId).map { response ->
            BaseResponse(
                ok = response.ok,
                error = response.error,
                message = response.message,
                data = response.data?.asModel()
            )
        }
}
