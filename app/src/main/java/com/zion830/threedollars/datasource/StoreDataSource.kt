package com.zion830.threedollars.datasource

import com.threedollar.common.base.BaseResponse
import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.store.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Response

interface StoreDataSource {

    suspend fun getAllStore(
        latitude: Double,
        longitude: Double,
    ): Response<NearStoreResponse>

    suspend fun getStoreDetail(
        storeId: Int,
        latitude: Double,
        longitude: Double,
        startDate: String?,
    ): Response<StoreDetailResponse>

    suspend fun getCategoryByDistance(
        category: String,
        latitude: Double,
        longitude: Double,
    ): Response<NearStoreResponse>

    suspend fun getCategoryByReview(
        category: String,
        latitude: Double,
        longitude: Double,
    ): Response<NearStoreResponse>

    suspend fun addReview(
        newReviewRequest: NewReviewRequest,
    ): Response<NewReviewResponse>

    suspend fun editReview(
        reviewId: Int,
        editReviewRequest: EditReviewRequest,
    ): Response<NewReviewResponse>

    suspend fun deleteReview(
        reviewId: Int,
    ): Response<BaseResponse<String>>

    suspend fun saveImage(
        storeId: Int,
        images: List<MultipartBody.Part>,
    ): Response<AddImageResponse>

    suspend fun deleteImage(imageId: Int): Response<BaseResponse<String>>

    suspend fun saveStore(
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse>

    suspend fun updateStore(
        storeId: Int,
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse>

    suspend fun deleteStore(
        storeId: Int,
        deleteReasonType: String,
    ): Response<DeleteStoreResponse>

    suspend fun getNearExist(
        latitude: Double,
        longitude: Double,
    ): Response<NearExistResponse>

    fun getCategories(): Flow<Response<CategoriesResponse>>

    suspend fun getBossNearStore(
        latitude: Double,
        longitude: Double,
    ): Response<BossNearStoreResponse>

    suspend fun getDistanceBossNearStore(
        categoryId: String,
        latitude: Double,
        longitude: Double,
    ): Response<BossNearStoreResponse>

    suspend fun getFeedbacksCountsBossNearStore(
        categoryId: String,
        latitude: Double,
        longitude: Double,
    ): Response<BossNearStoreResponse>

    suspend fun getBossStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double,
    ): Response<BossStoreDetailResponse>

    suspend fun getBossStoreFeedbackFull(bossStoreId: String): Response<BossStoreFeedbackFullResponse>
    suspend fun getBossStoreFeedbackType(): Response<BossStoreFeedbackTypeResponse>
    suspend fun postBossStoreFeedback(
        bossStoreId: String,
        bossStoreFeedbackRequest: BossStoreFeedbackRequest,
    ): Response<BaseResponse<String>>

    suspend fun addVisitHistory(newVisitHistory: NewVisitHistory): Response<BaseResponse<String>>

    suspend fun putFavorite(storeType: String, storeId: String): Response<BaseResponse<String>>

    suspend fun deleteFavorite(storeType: String, storeId: String): Response<BaseResponse<String>>
}