package com.zion830.threedollars.repository

import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.network.ServiceApi
import com.zion830.threedollars.repository.model.request.NewReview
import com.zion830.threedollars.repository.model.response.AllStoreResponse
import com.zion830.threedollars.repository.model.response.SearchByDistanceResponse
import com.zion830.threedollars.repository.model.response.SearchByReviewResponse
import com.zion830.threedollars.repository.model.response.StoreDetailResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call

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
    ): Call<ResponseBody> = service.saveReview(storeId, userId, newReview)

    suspend fun saveStore(
        storeInfo: Map<String, String>,
        images: List<MultipartBody.Part>
    ): Call<ResponseBody> = service.saveStore(storeInfo, images)

    suspend fun saveStore(
        storeInfo: Map<String, String>
    ): Call<ResponseBody> = service.saveStore(storeInfo)

    suspend fun updateStore(
        storeInfo: Map<String, String>,
        images: List<MultipartBody.Part>
    ): Call<ResponseBody> = service.updateStore(storeInfo, images)

    suspend fun updateStore(
        storeInfo: Map<String, String>
    ): Call<ResponseBody> = service.updateStore(storeInfo)

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