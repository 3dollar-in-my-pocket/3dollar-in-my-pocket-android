package com.zion830.threedollars.datasource

import com.threedollar.common.base.BaseResponse
import com.zion830.threedollars.Constants.DISTANCE_ASC
import com.zion830.threedollars.Constants.REVIEW_DESC
import com.zion830.threedollars.Constants.TOTAL_FEEDBACKS_COUNTS_DESC
import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.store.*
import com.zion830.threedollars.network.NewServiceApi
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class StoreDataSourceImpl @Inject constructor(private val newService: NewServiceApi) :
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
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse> = newService.saveStore(newStoreRequest)

    override suspend fun updateStore(
        storeId: Int,
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse> = newService.editStore(storeId, newStoreRequest)

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