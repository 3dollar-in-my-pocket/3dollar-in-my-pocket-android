package com.zion830.threedollars.repository

import com.zion830.threedollars.Constants.DISTANCE_ASC
import com.zion830.threedollars.Constants.REVIEW_DESC
import com.zion830.threedollars.network.NewServiceApi
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.repository.model.v2.request.EditReviewRequest
import com.zion830.threedollars.repository.model.v2.request.NewReviewRequest
import com.zion830.threedollars.repository.model.v2.request.NewStoreRequest
import com.zion830.threedollars.repository.model.v2.response.NewReviewResponse
import com.zion830.threedollars.repository.model.v2.response.store.*
import okhttp3.MultipartBody
import retrofit2.Response
import zion830.com.common.base.BaseResponse

class StoreRepository(
    private val newService: NewServiceApi = RetrofitBuilder.newServiceApi,
) {

    suspend fun getAllStore(
        latitude: Double,
        longitude: Double
    ): Response<NearStoreResponse> =
        newService.getNearStore(latitude, longitude, latitude, longitude)

    suspend fun getStoreDetail(
        storeId: Int,
        latitude: Double,
        longitude: Double,
        startDate: String?
    ): Response<StoreDetailResponse> =
        newService.getStoreInfo(latitude, longitude, storeId, startDate)

    suspend fun getCategoryByDistance(
        category: String,
        latitude: Double,
        longitude: Double
    ): Response<NearStoreResponse> = newService.getNearStore(
        latitude = latitude,
        longitude = longitude,
        mapLatitude = latitude,
        mapLongitude = longitude,
        orderType = DISTANCE_ASC,
        category = category
    )

    suspend fun getCategoryByReview(
        category: String,
        latitude: Double,
        longitude: Double
    ): Response<NearStoreResponse> =
        newService.getNearStore(
            latitude = latitude,
            longitude = longitude,
            mapLatitude = latitude,
            mapLongitude = longitude,
            orderType = REVIEW_DESC,
            category = category
        )

    suspend fun addReview(
        newReviewRequest: NewReviewRequest
    ): Response<NewReviewResponse> = newService.saveReview(newReviewRequest)

    suspend fun editReview(
        reviewId: Int,
        editReviewRequest: EditReviewRequest
    ): Response<NewReviewResponse> = newService.editReview(reviewId, editReviewRequest)

    suspend fun deleteReview(
        reviewId: Int
    ): Response<BaseResponse<String>> = newService.deleteReview(reviewId)

    suspend fun saveImage(
        storeId: Int,
        images: List<MultipartBody.Part>
    ): Response<AddImageResponse> = newService.saveImages(images, storeId)

    suspend fun deleteImage(
        imageId: Int
    ) = newService.deleteImage(imageId)

    suspend fun saveStore(
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse> = newService.saveStore(newStoreRequest)

    suspend fun updateStore(
        storeId: Int,
        newStoreRequest: NewStoreRequest
    ): Response<NewStoreResponse> = newService.editStore(storeId, newStoreRequest)

    suspend fun deleteStore(
        storeId: Int,
        deleteReasonType: String
    ): Response<DeleteStoreResponse> = newService.deleteStore(storeId, deleteReasonType)

    suspend fun getCategories() = newService.getCategories()

    suspend fun getNearExist(
        latitude: Double,
        longitude: Double
    ): Response<NearExistResponse> =
        newService.getNearExists(latitude = latitude, longitude = longitude)

    suspend fun getBossCategory(): Response<BossCategoriesResponse> = newService.getBossCategories()

    suspend fun getBossNearStore(
        latitude: Double,
        longitude: Double
    ): Response<BossNearStoreResponse> = newService.getBossNearStore(
        latitude = latitude,
        longitude = longitude,
        mapLatitude = latitude,
        mapLongitude = longitude,
        orderType = DISTANCE_ASC
    )

    suspend fun getBossStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double
    ): Response<BossStoreDetailResponse> = newService.getBossStoreDetail(
        bossStoreId = bossStoreId,
        latitude = latitude,
        longitude = longitude
    )

    suspend fun getBossStoreFeedbackFull(bossStoreId: String): Response<BossStoreFeedbackFullResponse> =
        newService.getBossStoreFeedbackFull(bossStoreId = bossStoreId)

    suspend fun getBossStoreFeedbackType(): Response<BossStoreFeedbackTypeResponse> =
        newService.getBossStoreFeedbackType()

    suspend fun postBossStoreFeedback(bossStoreId: String,bossStoreFeedbackRequest: BossStoreFeedbackRequest): Response<BaseResponse<String>> =
        newService.postBossStoreFeedback(bossStoreId,bossStoreFeedbackRequest)
}