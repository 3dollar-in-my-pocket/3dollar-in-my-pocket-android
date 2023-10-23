package com.home.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.home.data.asModel
import com.home.data.asRequest
import com.home.data.datasource.HomeRemoteDataSource
import com.home.data.datasource.ImagePagingDataSource
import com.home.data.datasource.ReviewPagingDataSource
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.*
import com.home.domain.data.user.UserModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.UserStoreModelRequest
import com.threedollar.common.base.BaseResponse
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
            ok = it.ok,
            data = it.data?.asModel(),
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

    override fun getBossStoreDetail(bossStoreId: String, deviceLatitude: Double, deviceLongitude: Double): Flow<BaseResponse<BossStoreDetailModel>> =
        homeRemoteDataSource.getBossStoreDetail(bossStoreId, deviceLatitude, deviceLongitude).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
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
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun getMyInfo(): Flow<BaseResponse<UserModel>> = homeRemoteDataSource.getMyInfo().map {
        BaseResponse(
            ok = it.ok,
            data = it.data?.asModel(),
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

    override fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.putMarketingConsent(MarketingConsentRequest(marketingConsent))

    override fun postPushInformation(pushToken: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postPushInformation(PushInformationRequest(pushToken = pushToken))

    override fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementModel>>> =
        homeRemoteDataSource.getAdvertisements(position).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.map { response -> response.asModel() },
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun putFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>> = homeRemoteDataSource.putFavorite(storeType, storeId)

    override fun deleteFavorite(storeType: String, storeId: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.deleteFavorite(storeType, storeId)

    override fun getFeedbackFull(targetType: String, targetId: String): Flow<BaseResponse<List<FoodTruckReviewModel>>> {
        val feedbackTypeResponseList = sharedPrefUtils.getList<FeedbackTypeResponse>(BOSS_FEED_BACK_LIST)
        return homeRemoteDataSource.getFeedbackFull(targetType, targetId).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.map { feedbackCountResponse -> feedbackCountResponse.asModel(feedbackTypeResponseList) },
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }
    }

    override fun postFeedback(targetType: String, targetId: String, postFeedbackRequest: List<String>): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postFeedback(
            targetType = targetType,
            targetId = targetId,
            postFeedbackRequest = PostFeedbackRequest(postFeedbackRequest)
        )

    override fun deleteStore(storeId: Int, deleteReasonType: String): Flow<BaseResponse<DeleteResultModel>> =
        homeRemoteDataSource.deleteStore(storeId, deleteReasonType).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun postStoreVisit(storeId: Int, visitType: String): Flow<BaseResponse<String>> =
        homeRemoteDataSource.postStoreVisit(PostStoreVisitRequest(storeId = storeId, type = visitType))

    override fun deleteImage(imageId: Int): Flow<BaseResponse<String>> = homeRemoteDataSource.deleteImage(imageId)

    override fun saveImages(images: List<MultipartBody.Part>, storeId: Int): Flow<BaseResponse<List<SaveImagesModel>>> =
        homeRemoteDataSource.saveImages(images, storeId).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.map { response -> response.asModel() },
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

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
                error = it.error
            )
        }

    override fun putStoreReview(reviewId: Int, contents: String, rating: Int): Flow<BaseResponse<EditStoreReviewModel>> =
        homeRemoteDataSource.putStoreReview(reviewId, StoreReviewRequest(contents = contents, rating = rating)).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
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
                error = it.error
            )
        }

    override fun postUserStore(userStoreModelRequest: UserStoreModelRequest): Flow<BaseResponse<PostUserStoreModel>> =
        homeRemoteDataSource.postUserStore(userStoreModelRequest.asRequest()).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun putUserStore(userStoreModelRequest: UserStoreModelRequest, storeId: Int): Flow<BaseResponse<PostUserStoreModel>> =
        homeRemoteDataSource.putUserStore(userStoreRequest = userStoreModelRequest.asRequest(), storeId = storeId).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }
}