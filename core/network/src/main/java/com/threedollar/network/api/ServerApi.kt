package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PushInformationRequest
import retrofit2.http.*

interface ServerApi {

    // User
    @GET("/api/v2/user/me")
    suspend fun getMyInfo(): BaseResponse<UserResponse>

    @PUT("/api/v1/user/me/marketing-consent")
    suspend fun putMarketingConsent(@Body marketingConsentRequest: MarketingConsentRequest): BaseResponse<String>

    // Store
    @GET("/api/v4/stores/around")
    suspend fun getAroundStores(
        @Query("distanceM") distanceM : Double = 100000.0,
        @Query("categoryIds") categoryIds : Array<String>? = null,
        @Query("targetStores") targetStores : Array<String>? = null,
        @Query("sortType") sortType : String,
        @Query("filterCertifiedStores") filterCertifiedStores : Boolean? = null,
        @Query("mapLatitude") mapLatitude : Double,
        @Query("mapLongitude") mapLongitude  : Double,
        @Header("X-Device-Latitude") deviceLatitude : Double,
        @Header("X-Device-Longitude") deviceLongitude : Double,
    ) : BaseResponse<AroundStoreResponse>

    // Device
    @POST("/api/v1/device")
    suspend fun postPushInformation(@Body informationRequest: PushInformationRequest): BaseResponse<String>

    // advertisement
    @GET("/api/v1/advertisements")
    suspend fun getAdvertisements(@Query("position") position: String): BaseResponse<List<AdvertisementResponse>>
}