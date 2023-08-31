package com.zion830.threedollars.network

import com.zion830.threedollars.Constants
import com.zion830.threedollars.Constants.FAVORITE_STORE
import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.FAQCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.PopupsResponse
import com.zion830.threedollars.datasource.model.v4.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.my.*
import com.zion830.threedollars.datasource.model.v2.response.store.*
import com.zion830.threedollars.datasource.model.v2.response.visit_history.MyVisitHistoryResponse
import com.zion830.threedollars.datasource.model.v4.aroundStore.AroundStoreResponse
import com.zion830.threedollars.datasource.model.v4.categories.CategoriesResponse
import com.zion830.threedollars.datasource.model.v4.districts.DistrictsResponse
import com.zion830.threedollars.datasource.model.v4.feedback.UserStoreFeedbackResponse
import com.zion830.threedollars.datasource.model.v4.nearExists.NearExistResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import zion830.com.common.base.BaseResponse

interface ServiceApi {

    // 특정 시의 구 목록을 조회합니다.
    @GET("/api/v1/regions/cities/{cityProvince}/districts")
    suspend fun getSpecificDistricts(
        @Path("cityProvince") cityProvince: String,
    ): Response<BaseResponse<DistrictsResponse>>

    // 전체 시와 구 목록을 조회합니다.
    @GET("/api/v1/regions/cities/districts")
    suspend fun getAllDistricts(): Response<BaseResponse<List<DistrictsResponse>>>


    // 주변에 등록된 가게가 존재하는지 확인합니다.
    @GET("/api/v1/stores/near/exists")
    suspend fun getNearExists(
        @Query("distance") distance: Double = 10.0,
        @Query("mapLatitude") latitude: Double,
        @Query("mapLongitude") longitude: Double,
    ): Response<BaseResponse<NearExistResponse>>

    // 가게 메뉴 전체 카테고리 목록을 조회합니다.
    @GET("/api/v4/store/categories")
    suspend fun getCategories(): Response<BaseResponse<List<CategoriesResponse>>>

    // 주변의 가게 목록을 조회합니다.
    /*
        1. categoryIds가 null인 경우 모든 카테고리를 조회합니다.
        2. targetStores를 넘기는 경우 해당하는 가게만 필터링되서 조회합니다(default 전체 가게 조회)
        3. StoreAroundSortType Enum은 Enum API를 사용하는 경우 동적으로 관리할 수 있습니다
     */
    @GET("/api/v4/stores/around")
    suspend fun getAroundStores(
        @Query("distanceM") distance: Double = 10.0,
        @Query("categoryIds") categoryIds: Array<String>? = null,
        @Query("targetStores") targetStores: Array<String>,
        @Query("sortType") sortType: String,
        @Query("filterCertifiedStores") filterCertifiedStores: Boolean = false,
        @Query("size") size: Int = 20,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("X-Device-Latitude") deviceLatitude: Double,
        @Query("X-Device-Longitude") deviceLongitude: Double,
    ): Response<BaseResponse<AroundStoreResponse>>

    // 유저의 가게 즐겨찾기 폴더를 조회합니다.
    @GET("/api/v1/favorite/store/folder/my")
    suspend fun getMyFavoriteFolder(
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
    ): Response<BaseResponse<MyFavoriteFolderResponse>>

    // 가게 즐겨찾기 폴더를 조회합니다.
    @GET("/api/v1/favorite/store/folder/target/{favoriteFolderId}")
    suspend fun getFavoriteViewer(
        @Path("favoriteFolderId") id: String,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int = 20,
    ): Response<BaseResponse<MyFavoriteFolderResponse>>

    // 가게 즐겨찾기를 전체 취소합니다.
    @DELETE("/api/v1/favorite/subscription/store/clear")
    suspend fun deleteAllFavorite(): Response<BaseResponse<String>>

    // 가게를 즐겨찾기로 등록합니다.
    @PUT("/api/v1/favorite/subscription/store/target/{storeType}/{storeId}")
    suspend fun putFavorite(@Path("storeType") storeType: String, @Path("storeId") storeId: String): Response<BaseResponse<String>>

    // 가게를 즐겨찾기에서 삭제 합니다.
    @DELETE("/api/v1/favorite/subscription/store/target/{storeType}/{storeId}")
    suspend fun deleteFavorite(@Path("storeType") storeType: String, @Path("storeId") storeId: String): Response<BaseResponse<String>>

    // 유저가 가게에 등록한 피드백 목록을 조회합니다
    @GET("/api/v1/user/store/{targetType}/feedbacks")
    suspend fun getUserStoreFeedback(
        @Path("targetType") targetType: String,
        @Query("size") size: Int,
        @Query("cursor") cursor: String = "",
    ) : Response<BaseResponse<UserStoreFeedbackResponse>>

    @PUT("/api/v2/store/review/{reviewId}")
    suspend fun editReview(
        @Path("reviewId") reviewId: Int,
        @Body editReviewRequest: EditReviewRequest,
    ): Response<NewReviewResponse>

    @DELETE("/api/v2/store/review/{reviewId}")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Int,
    ): Response<BaseResponse<String>>

    // 가게
    @POST("/api/v2/store")
    suspend fun saveStore(
        @Body newStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse>

    @POST("/api/v2/store/visit")
    suspend fun addVisitHistory(
        @Body newVisitHistory: NewVisitHistory,
    ): Response<BaseResponse<String>>

    @PUT("/api/v2/store/{storeId}")
    suspend fun editStore(
        @Path("storeId") storeId: Int,
        @Body editStoreRequest: NewStoreRequest,
    ): Response<NewStoreResponse>

    @DELETE("/api/v2/store/{storeId}")
    suspend fun deleteStore(
        @Path("storeId") storeId: Int,
        @Query("deleteReasonType") deleteReasonType: String = "WRONG_CONTENT",
    ): Response<DeleteStoreResponse>

    @DELETE("/api/v2/store/image/{imageId}")
    suspend fun deleteImage(@Path("imageId") imageId: Int): Response<BaseResponse<String>>

    @POST("/api/v2/store/images")
    @Multipart
    suspend fun saveImages(
        @Part images: List<MultipartBody.Part>,
        @Query("storeId") storeId: Int,
    ): Response<AddImageResponse>

    // 가게 검색
    @GET("/api/v2/store")
    suspend fun getStoreInfo(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("storeId") storeId: Int,
        @Query("startDate") startDate: String?,
    ): Response<StoreDetailResponse>

    @GET("/api/v2/stores/near")
    suspend fun getNearStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("distance") distance: Double = 100000.0,
        @Query("orderType") orderType: String = Constants.DISTANCE_ASC,
        @Query("category") category: String = "",
    ): Response<NearStoreResponse>

    @GET("/api/v2/store/{storeId}/images")
    suspend fun getStoreImages(
        @Query("storeId") storeId: Int,
    ): Response<AddImageResponse>

    @GET("/api/v2/stores/me")
    suspend fun getMyStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("cachingTotalElements") cachingTotalElements: Int,
        @Query("cursor") cursor: Int,
        @Query("size") size: Int = 100,
    ): Response<MyStoreResponse>

    @GET("/api/v3/stores/me")
    suspend fun getMyStore(
        @Query("cursor") cursor: Int?,
        @Query("size") size: Int = 20,
    ): Response<MyStoreResponse>

    // 사용자
    @DELETE("/api/v2/signout")
    suspend fun signOut(): Response<BaseResponse<String>>

    @POST("/api/v2/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignResponse>

    @POST("/api/v2/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<BaseResponse<SignUser>>

    @POST("/api/v2/logout")
    suspend fun logout(): Response<BaseResponse<String>>

    @GET("/api/v2/user/me")
    suspend fun getMyInfo(): Response<MyInfoResponse>

    @GET("/api/v1/user/activity")
    suspend fun getUserActivity(): Response<UserActivityResponse>

    @GET("/api/v2/store/visits/me")
    suspend fun getMyVisitHistory(
        @Query("cursor") cursor: Int?,
        @Query("size") size: Int,
    ): Response<MyVisitHistoryResponse>

    @PUT("/api/v2/user/me")
    suspend fun editNickname(@Body editNameRequest: EditNameRequest): Response<MyInfoResponse>

    @GET("/api/v2/user/name/check")
    suspend fun checkNickname(@Query("name") name: String): Response<BaseResponse<String>>

    @GET("/api/v1/medals")
    suspend fun getMedals(): Response<BaseResponse<List<Medal>>>

    @GET("/api/v1/user/medals")
    suspend fun getMyMedals(): Response<BaseResponse<List<Medal>>>

    @PUT("/api/v1/user/medal")
    suspend fun updateMyMedals(@Body updateMedalRequest: UpdateMedalRequest): Response<BaseResponse<User>>

    // faq
    @GET("/api/v2/faq/categories")
    suspend fun getFAQCategory(): Response<FAQCategoryResponse>

    @GET("/api/v2/faqs")
    suspend fun getFAQByCategory(@Query("category") category: String): Response<FAQByCategoryResponse>

    @GET("/api/v1/advertisements")
    suspend fun getPopups(
        @Query("position") position: String,
        @Query("size") size: Int? = null,
    ): Response<PopupsResponse>


    @GET("/api/v1/boss/stores/around")
    suspend fun getBossNearStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("distanceKm") distance: Double = 100000.0,
        @Query("orderType") orderType: String = Constants.DISTANCE_ASC,
    ): Response<BossNearStoreResponse>

    @GET("/api/v1/boss/stores/around")
    suspend fun getCategoryIdBossNearStore(
        @Query("categoryId") categoryId: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("distanceKm") distance: Double = 100000.0,
        @Query("orderType") orderType: String = Constants.DISTANCE_ASC,
    ): Response<BossNearStoreResponse>

    @GET("/api/v1/boss/store/{bossStoreId}")
    suspend fun getBossStoreDetail(
        @Path("bossStoreId") bossStoreId: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Response<BossStoreDetailResponse>

    @GET("/api/v1/boss/store/{bossStoreId}/feedbacks/full")
    suspend fun getBossStoreFeedbackFull(
        @Path("bossStoreId") bossStoreId: String,
    ): Response<BossStoreFeedbackFullResponse>

    @GET("/api/v1/boss/store/feedback/types")
    suspend fun getBossStoreFeedbackType(): Response<BossStoreFeedbackTypeResponse>

    @POST("/api/v1/boss/store/{bossStoreId}/feedback")
    suspend fun postBossStoreFeedback(
        @Path("bossStoreId") bossStoreId: String,
        @Body feedbackTypes: BossStoreFeedbackRequest,
    ): Response<BaseResponse<String>>

    @POST("/api/v1/device")
    suspend fun postPushInformation(@Body informationRequest: PushInformationRequest): Response<BaseResponse<String>>

    @DELETE("/api/v1/device")
    suspend fun deletePushInformation(): Response<BaseResponse<String>>

    @PUT("/api/v1/device/token")
    suspend fun putPushInformationToken(@Body informationTokenRequest: PushInformationTokenRequest): Response<BaseResponse<String>>

    @PUT("/api/v1/user/me/marketing-consent")
    suspend fun putMarketingConsent(@Body marketingConsentRequest: MarketingConsentRequest): Response<BaseResponse<String>>


    @POST("/api/v1/event/click/{targetType}/{targetId}")
    suspend fun eventClick(@Path("targetType") targetType: String, @Path("targetId") targetId: String): Response<BaseResponse<String>>

    @DELETE("/api/v1/favorite/{favoriteType}/folder")
    suspend fun allDeleteFavorite(@Path("favoriteType") favoriteType: String = FAVORITE_STORE): Response<BaseResponse<String>>

    @PUT("/api/v1/favorite/{favoriteType}/folder")
    suspend fun updateFavoriteInfo(
        @Path("favoriteType") favoriteType: String = FAVORITE_STORE,
        @Body favoriteInfoRequest: FavoriteInfoRequest,
    ): Response<BaseResponse<String>>
}