package com.home.domain.repository

import androidx.paging.PagingData
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.*
import com.home.domain.data.user.UserModel
import com.home.domain.request.ReportReasonsGroupType
import com.home.domain.request.ReportReviewModelRequest
import com.home.domain.request.UserStoreModelRequest
import com.threedollar.common.base.BaseResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

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

    fun deleteImage(imageId: Int): Flow<BaseResponse<String>>

    fun saveImages(images: List<MultipartBody.Part>, storeId: Int): Flow<BaseResponse<List<SaveImagesModel>>>

    fun getStoreImages(storeId: Int): Flow<PagingData<ImageContentModel>>

    fun postStoreReview(contents: String, rating: Int?, storeId: Int): Flow<BaseResponse<ReviewContentModel>>

    fun putStoreReview(reviewId: Int, contents: String, rating: Int): Flow<BaseResponse<EditStoreReviewModel>>

    fun getStoreReview(storeId: Int, reviewSortType: ReviewSortType): Flow<PagingData<ReviewContentModel>>

    fun getStoreNearExists(distance: Double, mapLatitude: Double, mapLongitude: Double): Flow<BaseResponse<StoreNearExistsModel>>

    fun postUserStore(userStoreModelRequest: UserStoreModelRequest): Flow<BaseResponse<PostUserStoreModel>>

    fun putUserStore(userStoreModelRequest: UserStoreModelRequest, storeId: Int): Flow<BaseResponse<PostUserStoreModel>>


    fun reportStoreReview(storeId: Int, reviewId: Int, reportReviewModelRequest: ReportReviewModelRequest): Flow<BaseResponse<String>>

    fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsModel>>
}