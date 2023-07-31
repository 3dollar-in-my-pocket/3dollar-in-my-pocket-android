package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.datasource.model.v2.request.EditReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.NewReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.NewStoreRequest
import com.zion830.threedollars.datasource.model.v2.request.NewVisitHistory
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.store.AddImageResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossCategoriesResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreDetailResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackFullResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackTypeResponse
import com.zion830.threedollars.datasource.model.v2.response.store.CategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.store.DeleteStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NearExistResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NewStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.StoreDetailResponse
import okhttp3.MultipartBody
import retrofit2.Response
import zion830.com.common.base.BaseResponse

interface StoreDataSource {

    suspend fun getAllStore(
        latitude: Double,
        longitude: Double
    ): Response<NearStoreResponse>

    suspend fun getStoreDetail(
        storeId: Int,
        latitude: Double,
        longitude: Double,
        startDate: String?
    ): Response<StoreDetailResponse>

    suspend fun getCategoryByDistance(
        category: String,
        latitude: Double,
        longitude: Double
    ): Response<NearStoreResponse>

    suspend fun getCategoryByReview(
        category: String,
        latitude: Double,
        longitude: Double
    ): Response<NearStoreResponse>

    suspend fun addReview(
        newReviewRequest: NewReviewRequest
    ): Response<NewReviewResponse>

    suspend fun editReview(
        reviewId: Int,
        editReviewRequest: EditReviewRequest
    ): Response<NewReviewResponse>

    suspend fun deleteReview(
        reviewId: Int
    ): Response<BaseResponse<String>>

    suspend fun saveImage(
        storeId: Int,
        images: List<MultipartBody.Part>
    ): Response<AddImageResponse>

    suspend fun deleteImage(imageId: Int): Response<BaseResponse<String>>

    suspend fun saveStore(
        newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse>

    suspend fun updateStore(
        storeId: Int,
        newStoreRequest: NewStoreRequest
    ): Response<NewStoreResponse>

    suspend fun deleteStore(
        storeId: Int,
        deleteReasonType: String
    ): Response<DeleteStoreResponse>

    suspend fun getCategories(): Response<CategoryResponse>

    suspend fun getNearExist(
        latitude: Double,
        longitude: Double
    ): Response<NearExistResponse>

    suspend fun getBossCategory(): Response<BossCategoriesResponse>

    suspend fun getBossNearStore(
        latitude: Double,
        longitude: Double
    ): Response<BossNearStoreResponse>

    suspend fun getDistanceBossNearStore(
        categoryId: String,
        latitude: Double,
        longitude: Double
    ): Response<BossNearStoreResponse>

    suspend fun getFeedbacksCountsBossNearStore(
        categoryId: String,
        latitude: Double,
        longitude: Double
    ): Response<BossNearStoreResponse>

    suspend fun getBossStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double
    ): Response<BossStoreDetailResponse>

    suspend fun getBossStoreFeedbackFull(bossStoreId: String): Response<BossStoreFeedbackFullResponse>
    suspend fun getBossStoreFeedbackType(): Response<BossStoreFeedbackTypeResponse>
    suspend fun postBossStoreFeedback(
        bossStoreId: String,
        bossStoreFeedbackRequest: BossStoreFeedbackRequest
    ): Response<BaseResponse<String>>

    suspend fun addVisitHistory(newVisitHistory: NewVisitHistory): Response<BaseResponse<String>>

    suspend fun putFavorite(storeType: String, storeId: String): Response<BaseResponse<String>>

    suspend fun deleteFavorite(storeType: String, storeId: String): Response<BaseResponse<String>>
}