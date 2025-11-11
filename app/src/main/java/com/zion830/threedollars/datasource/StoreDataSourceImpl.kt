package com.zion830.threedollars.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.Constants.DISTANCE_ASC
import com.threedollar.network.data.store.StoreReviewDetailResponse
import com.threedollar.network.request.BossStoreReviewRequest
import com.threedollar.common.utils.Constants.REVIEW_DESC
import com.threedollar.common.utils.Constants.TOTAL_FEEDBACKS_COUNTS_DESC
import com.zion830.threedollars.datasource.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.datasource.model.v2.request.EditReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.NewReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.NewStoreRequest
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreDetailResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackFullResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackTypeResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NewStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.StoreDetailResponse
import com.zion830.threedollars.network.NewServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
        category = category,
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
            category = category,
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

    override suspend fun saveStore(
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse> = newService.saveStore(newStoreRequest)

    override suspend fun updateStore(
        storeId: Int,
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse> = newService.editStore(storeId, newStoreRequest)

    override fun getCategories() = flow { emit(newService.getCategories()) }

    override suspend fun getBossNearStore(
        latitude: Double,
        longitude: Double,
    ): Response<BossNearStoreResponse> = newService.getBossNearStore(
        latitude = latitude,
        longitude = longitude,
        mapLatitude = latitude,
        mapLongitude = longitude,
        orderType = DISTANCE_ASC,
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
        orderType = DISTANCE_ASC,
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
        orderType = TOTAL_FEEDBACKS_COUNTS_DESC,
    )

    override suspend fun getBossStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double,
    ): Response<BossStoreDetailResponse> = newService.getBossStoreDetail(
        bossStoreId = bossStoreId,
        latitude = latitude,
        longitude = longitude,
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

    override fun postBossStoreReview(
        bossStoreReviewRequest: BossStoreReviewRequest
    ): Flow<BaseResponse<StoreReviewDetailResponse>> = flow {
        val response = newService.postBossStoreReview(bossStoreReviewRequest)
        if (response.isSuccessful) {
            response.body()?.let { emit(it) }
        } else {
            emit(BaseResponse(ok = false, message = "네트워크 오류가 발생했습니다.", data = null))
        }
    }
}
