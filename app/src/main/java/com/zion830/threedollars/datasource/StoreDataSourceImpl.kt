package com.zion830.threedollars.datasource

import com.zion830.threedollars.Constants.DISTANCE_ASC
import com.zion830.threedollars.Constants.REVIEW_DESC
import com.zion830.threedollars.Constants.TOTAL_FEEDBACKS_COUNTS_DESC
import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.store.*
import com.zion830.threedollars.datasource.model.v4.nearExists.NearExistResponse
import com.zion830.threedollars.datasource.model.v4.store.DeleteStoreResponse
import com.zion830.threedollars.datasource.model.v4.store.request.StoreRequest
import com.zion830.threedollars.network.ServiceApi
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Response
import zion830.com.common.base.BaseResponse
import javax.inject.Inject

class StoreDataSourceImpl @Inject constructor(private val newService: ServiceApi) :
    StoreDataSource {

    override suspend fun getAllStore(
        latitude: Double,
        longitude: Double,
    ): Response<NearStoreResponse> =
        newService.getNearStore(latitude, longitude, latitude, longitude)

    override suspend fun getStoreDetail(
        storeId: Int,
        latitude: Double,
        longitude: Double,
        startDate: String?,
    ): Response<StoreDetailResponse> =
        newService.getStoreInfo(latitude, longitude, storeId, startDate)

    override suspend fun getCategoryByDistance(
        category: String,
        latitude: Double,
        longitude: Double,
    ): Response<NearStoreResponse> = newService.getNearStore(
        latitude = latitude,
        longitude = longitude,
        mapLatitude = latitude,
        mapLongitude = longitude,
        orderType = DISTANCE_ASC,
        category = category
    )

    override suspend fun getCategoryByReview(
        category: String,
        latitude: Double,
        longitude: Double,
    ): Response<NearStoreResponse> =
        newService.getNearStore(
            latitude = latitude,
            longitude = longitude,
            mapLatitude = latitude,
            mapLongitude = longitude,
            orderType = REVIEW_DESC,
            category = category
        )

    override suspend fun addReview(
        newReviewRequest: NewReviewRequest,
    ): Response<NewReviewResponse> = newService.saveReview(newReviewRequest)

    override suspend fun editReview(
        reviewId: Int,
        editReviewRequest: EditReviewRequest,
    ): Response<NewReviewResponse> = newService.editReview(reviewId, editReviewRequest)

    override suspend fun deleteReview(
        reviewId: Int,
    ): Response<BaseResponse<String>> = newService.deleteReview(reviewId)

    override suspend fun saveImage(
        storeId: Int,
        images: List<MultipartBody.Part>,
    ): Response<AddImageResponse> = newService.saveImages(images, storeId)

    override suspend fun deleteImage(
        imageId: Int,
    ) = newService.deleteImage(imageId)

    override suspend fun saveStore(
        storeRequest: StoreRequest,
    ): Response<NewStoreResponse> = newService.postStore(storeRequest)

    override suspend fun updateStore(
        storeId: Int,
        storeRequest: StoreRequest,
    ): Response<NewStoreResponse> = newService.editStore(storeId, storeRequest)

    override suspend fun deleteStore(
        storeId: Int,
        deleteReasonType: String,
    ): Response<DeleteStoreResponse> = newService.deleteStore(storeId, deleteReasonType)

    override suspend fun getNearExist(
        latitude: Double,
        longitude: Double,
    ): Response<NearExistResponse> =
        newService.getNearExists(latitude = latitude, longitude = longitude)

    override fun getCategories() = flow { emit(newService.getCategories()) }

    override suspend fun getBossNearStore(
        latitude: Double,
        longitude: Double,
    ): Response<BossNearStoreResponse> = newService.getBossNearStore(
        latitude = latitude,
        longitude = longitude,
        mapLatitude = latitude,
        mapLongitude = longitude,
        orderType = DISTANCE_ASC
    )

    override suspend fun getDistanceBossNearStore(
        categoryId: String,
        latitude: Double,
        longitude: Double,
    ): Response<BossNearStoreResponse> = newService.getCategoryIdBossNearStore(
        categoryId = categoryId,
        latitude = latitude,
        longitude = longitude,
        mapLatitude = latitude,
        mapLongitude = longitude,
        orderType = DISTANCE_ASC
    )

    override suspend fun getFeedbacksCountsBossNearStore(
        categoryId: String,
        latitude: Double,
        longitude: Double,
    ): Response<BossNearStoreResponse> = newService.getCategoryIdBossNearStore(
        categoryId = categoryId,
        latitude = latitude,
        longitude = longitude,
        mapLatitude = latitude,
        mapLongitude = longitude,
        orderType = TOTAL_FEEDBACKS_COUNTS_DESC
    )

    override suspend fun getBossStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double,
    ): Response<BossStoreDetailResponse> = newService.getBossStoreDetail(
        bossStoreId = bossStoreId,
        latitude = latitude,
        longitude = longitude
    )

    override suspend fun getBossStoreFeedbackFull(bossStoreId: String): Response<BossStoreFeedbackFullResponse> =
        newService.getBossStoreFeedbackFull(bossStoreId = bossStoreId)

    override suspend fun getBossStoreFeedbackType(): Response<BossStoreFeedbackTypeResponse> =
        newService.getBossStoreFeedbackType()

    override suspend fun postBossStoreFeedback(
        bossStoreId: String,
        bossStoreFeedbackRequest: BossStoreFeedbackRequest,
    ): Response<BaseResponse<String>> =
        newService.postBossStoreFeedback(bossStoreId, bossStoreFeedbackRequest)

    override suspend fun addVisitHistory(newVisitHistory: NewVisitHistory): Response<BaseResponse<String>> =
        newService.addVisitHistory(newVisitHistory)

    override suspend fun putFavorite(storeType: String, storeId: String): Response<BaseResponse<String>> =
        newService.putFavorite(storeType, storeId)

    override suspend fun deleteFavorite(storeType: String, storeId: String): Response<BaseResponse<String>> =
        newService.deleteFavorite(storeType, storeId)
}