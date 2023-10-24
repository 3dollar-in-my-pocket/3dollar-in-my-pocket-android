package com.home.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackCountResponse
import com.threedollar.network.data.store.*
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

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
        deleteReasonType: String,
    ): Flow<BaseResponse<DeleteResultResponse>>

    fun postStoreVisit(postStoreVisitRequest: PostStoreVisitRequest): Flow<BaseResponse<String>>

    fun deleteImage(imageId: Int): Flow<BaseResponse<String>>

    fun saveImages(images: List<MultipartBody.Part>, storeId: Int): Flow<BaseResponse<List<SaveImagesResponse>>>

    fun postStoreReview(storeReviewRequest: StoreReviewRequest): Flow<BaseResponse<ReviewContent>>

    fun putStoreReview(reviewId: Int, storeReviewRequest: StoreReviewRequest): Flow<BaseResponse<EditStoreReviewResponse>>

    fun getStoreNearExists(distance: Double, mapLatitude: Double, mapLongitude: Double): Flow<BaseResponse<StoreNearExistResponse>>

    fun postUserStore(userStoreRequest: UserStoreRequest): Flow<BaseResponse<PostUserStoreResponse>>

    fun putUserStore(userStoreRequest: UserStoreRequest, storeId: Int) : Flow<BaseResponse<PostUserStoreResponse>>

}