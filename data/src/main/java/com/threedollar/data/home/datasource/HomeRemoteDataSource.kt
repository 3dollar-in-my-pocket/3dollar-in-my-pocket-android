package com.threedollar.data.home.datasource

import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.feedback.FeedbackExistsResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.store.BossStoreResponse
import com.threedollar.network.data.store.DeleteResultResponse
import com.threedollar.network.data.store.EditStoreReviewResponse
import com.threedollar.network.data.store.PostUserStoreResponse
import com.threedollar.network.data.store.StoreReviewDetailResponse
import com.threedollar.network.data.store.SaveImagesResponse
import com.threedollar.network.data.store.UploadFileResponse
import com.threedollar.network.data.store.UserStoreResponse
import com.threedollar.domain.home.data.store.UploadFileModel
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
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface HomeRemoteDataSource {

    fun getAroundStores(
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
    ): Flow<BaseResponse<AroundStoreResponse>>

    fun getBossStoreDetail(
        bossStoreId: String,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<BossStoreResponse>>

    fun getMyInfo(): Flow<BaseResponse<UserResponse>>

    fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest): Flow<BaseResponse<String>>

    fun putPushInformation(informationRequest: PushInformationRequest): Flow<BaseResponse<String>>

    fun getAdvertisements(
        position: AdvertisementsPosition,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<AdvertisementResponse>>

    fun putFavorite(storeId: String): Flow<BaseResponse<String>>

    fun deleteFavorite(storeId: String): Flow<BaseResponse<String>>

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
        deleteReasonType: String,
    ): Flow<BaseResponse<DeleteResultResponse>>

    fun postStoreVisit(postStoreVisitRequest: PostStoreVisitRequest): Flow<BaseResponse<String>>

    fun deleteImage(imageId: Int): Flow<BaseResponse<String>>

    suspend fun saveImages(images: List<MultipartBody.Part>, storeId: Int): BaseResponse<List<SaveImagesResponse>>

    suspend fun uploadFilesBulk(fileType: String, files: List<MultipartBody.Part>): BaseResponse<List<UploadFileResponse>>

    suspend fun uploadFile(fileType: String, file: MultipartBody.Part): BaseResponse<UploadFileResponse>

    fun postStoreReview(storeReviewRequest: StoreReviewRequest): Flow<BaseResponse<StoreReviewDetailResponse>>

    fun putStoreReview(reviewId: Int, storeReviewRequest: StoreReviewRequest): Flow<BaseResponse<EditStoreReviewResponse>>

    fun getStoreNearExists(distance: Double, mapLatitude: Double, mapLongitude: Double): Flow<BaseResponse<AroundStoreResponse>>

    fun postUserStore(userStoreRequest: UserStoreRequest): Flow<BaseResponse<PostUserStoreResponse>>

    fun putUserStore(userStoreRequest: UserStoreRequest, storeId: Int): Flow<BaseResponse<PostUserStoreResponse>>

    fun reportStoreReview(storeId: Int, reviewId: Int, reportReviewRequest: ReportReviewRequest): Flow<BaseResponse<String>>

    fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsResponse>>

    fun postPlace(placeRequest: PlaceRequest, placeType: PlaceType): Flow<BaseResponse<String>>

    fun deletePlace(placeType: PlaceType, placeId: String): Flow<BaseResponse<String>>

    fun putStickers(storeId: String, reviewId: String, stickers:List<String>): Flow<BaseResponse<String>>

    fun postBossStoreReview(storeId: String, contents: String, rating: Int, images: List<UploadFileModel>, feedbacks: List<String>): Flow<BaseResponse<StoreReviewDetailResponse>>

    fun checkFeedbackExists(targetType: String, targetId: String): Flow<BaseResponse<FeedbackExistsResponse>>
}