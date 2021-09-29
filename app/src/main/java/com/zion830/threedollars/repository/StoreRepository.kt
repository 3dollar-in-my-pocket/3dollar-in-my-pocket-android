package com.zion830.threedollars.repository

import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.network.ServiceApi
import com.zion830.threedollars.repository.model.request.NewReview
import com.zion830.threedollars.repository.model.request.NewStore
import com.zion830.threedollars.repository.model.response.*
import com.zion830.threedollars.ui.report_store.DeleteType
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Query
import retrofit2.http.QueryMap

class StoreRepository(
    private val service: ServiceApi = RetrofitBuilder.service
) {

    suspend fun getAllStore(
        latitude: Double,
        longitude: Double
    ): Call<AllStoreResponse> = service.getAllStore(latitude, longitude)

    suspend fun getStoreDetail(
        storeId: Int,
        latitude: Double,
        longitude: Double
    ): Call<StoreDetailResponse> = service.getStoreDetail(storeId, latitude, longitude)

    suspend fun addReview(
        storeId: Int,
        userId: Int,
        newReview: NewReview
    ): Call<ResponseBody?> = service.saveReview(storeId, userId, newReview)

    suspend fun editReview(
        reviewId: Int,
        newReview: NewReview
    ): Call<ResponseBody?> = service.editReview(reviewId, newReview)

    suspend fun deleteReview(
        reviewId: Int
    ): Call<ResponseBody?> = service.deleteReview(reviewId)

    suspend fun saveImage(
        storeId: Int,
        image: MultipartBody.Part
    ): Call<AddImageResponse> = service.saveImage(storeId, image)

    suspend fun deleteImage(
        storeId: Int,
        imageId: Int
    ) = service.deleteImage(storeId, imageId)

    suspend fun saveStore(
        @Query("categories") categories: List<String>,
        @Query("appearanceDays") appearanceDays: List<String>,
        @Query("paymentMethods") paymentMethods: List<String>,
        @QueryMap queryMap: Map<String, String>,
    ): Call<AddStoreResponse> = service.saveStore(categories, appearanceDays, paymentMethods, queryMap)

    suspend fun saveStore(
        newStore: NewStore
    ): Call<AddStoreResponse> = service.saveStore(newStore)

    suspend fun updateStore(
        @Query("categories") categories: List<String>,
        @Query("appearanceDays") appearanceDays: List<String>,
        @Query("paymentMethods") paymentMethods: List<String>,
        @QueryMap storeInfo: Map<String, String>
    ): Call<ResponseBody> = service.updateStore(categories, appearanceDays, paymentMethods, storeInfo)

    suspend fun deleteStore(
        deleteReasonType: DeleteType,
        storeId: Int,
        userId: Int
    ): Call<ResponseBody?> = service.deleteStore(deleteReasonType.key, storeId, userId)

    suspend fun getCategoryByDistance(
        category: String,
        latitude: Double,
        longitude: Double
    ): Call<SearchByDistanceResponse> = service.getCategoryByDistance(category, latitude, longitude)

    suspend fun getCategoryByReview(
        category: String,
        latitude: Double,
        longitude: Double
    ): Call<SearchByReviewResponse> = service.getCategoryByReview(category, latitude, longitude)
}