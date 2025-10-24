package com.threedollar.domain.home.repository

import androidx.paging.PagingData
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
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.threedollar.domain.home.request.PlaceRequest
import com.threedollar.domain.home.request.PlaceType
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.threedollar.domain.home.request.UserStoreModelRequest
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Path

interface HomeRepository {

    fun getAroundStores(
        distanceM:Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        sortType: String,
        filterCertifiedStores: Boolean?,
        filterConditionsTypeModel: List<FilterConditionsTypeModel>,
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

    fun putPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getAdvertisements(
        position: AdvertisementsPosition,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<List<AdvertisementModelV2>>>

    fun putFavorite(storeId: String): Flow<BaseResponse<String>>

    fun deleteFavorite(storeId: String): Flow<BaseResponse<String>>

    fun getFeedbackFull(targetType: String, targetId: String): Flow<BaseResponse<List<FoodTruckReviewModel>>>

    fun postFeedback(targetType: String, targetId: String, postFeedbackRequest: List<String>): Flow<BaseResponse<String>>

    fun deleteStore(storeId: Int, deleteReasonType: String): Flow<BaseResponse<DeleteResultModel>>

    fun postStoreVisit(storeId: Int, visitType: String): Flow<BaseResponse<String>>

    fun deleteImage(imageId: Int): Flow<BaseResponse<String>>

    suspend fun saveImages(images: List<MultipartBody.Part>, storeId: Int): BaseResponse<List<SaveImagesModel>>?

    suspend fun uploadFilesBulk(fileType: String, files: List<MultipartBody.Part>): BaseResponse<List<UploadFileModel>>?

    suspend fun uploadFile(fileType: String, file: MultipartBody.Part): BaseResponse<UploadFileModel>?

    fun getStoreImages(storeId: Int): Flow<PagingData<ImageContentModel>>

    fun postStoreReview(contents: String, rating: Int?, storeId: Int): Flow<BaseResponse<ReviewContentModel>>

    fun putStoreReview(reviewId: Int, contents: String, rating: Int): Flow<BaseResponse<EditStoreReviewModel>>

    fun getStoreReview(storeId: Int, reviewSortType: ReviewSortType): Flow<PagingData<ReviewContentModel>>

    fun getStoreNearExists(distance: Double, mapLatitude: Double, mapLongitude: Double): Flow<BaseResponse<StoreNearExistsModel>>

    fun postUserStore(userStoreModelRequest: UserStoreModelRequest): Flow<BaseResponse<PostUserStoreModel>>

    fun putUserStore(userStoreModelRequest: UserStoreModelRequest, storeId: Int): Flow<BaseResponse<PostUserStoreModel>>


    fun reportStoreReview(storeId: Int, reviewId: Int, reportReviewModelRequest: ReportReviewModelRequest): Flow<BaseResponse<String>>

    fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsModel>>

    fun postPlace(placeRequest: PlaceRequest, placeType: PlaceType): Flow<BaseResponse<String>>

    fun deletePlace(placeType: PlaceType, placeId: String): Flow<BaseResponse<String>>

    fun getPlace(placeType: PlaceType): Flow<PagingData<PlaceModel>>

    fun putStickers(storeId: String, reviewId: String, stickers:List<String>): Flow<BaseResponse<String>>

    fun postBossStoreReview(storeId: String, contents: String, rating: Int, images: List<UploadFileModel>, feedbacks: List<String>): Flow<BaseResponse<ReviewContentModel>>

    fun checkFeedbackExists(targetType: String, targetId: String): Flow<BaseResponse<FeedbackExistsModel>>
}