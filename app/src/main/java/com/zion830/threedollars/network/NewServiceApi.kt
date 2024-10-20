package com.zion830.threedollars.network

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.FAVORITE_STORE
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.data.visit_history.MyVisitHistoryResponse
import com.threedollar.network.request.PatchPushInformationRequest
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.datasource.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.datasource.model.v2.request.EditNameRequest
import com.zion830.threedollars.datasource.model.v2.request.EditReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.FavoriteInfoRequest
import com.zion830.threedollars.datasource.model.v2.request.LoginRequest
import com.zion830.threedollars.datasource.model.v2.request.NewReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.NewStoreRequest
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.datasource.model.v2.request.SignUpRequest
import com.zion830.threedollars.datasource.model.v2.request.UpdateMedalRequest
import com.zion830.threedollars.datasource.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.FAQCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.my.Medal
import com.zion830.threedollars.datasource.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.datasource.model.v2.response.my.MyReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.my.MyStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.my.SignResponse
import com.zion830.threedollars.datasource.model.v2.response.my.SignUser
import com.zion830.threedollars.datasource.model.v2.response.my.User
import com.zion830.threedollars.datasource.model.v2.response.my.UserActivityResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreDetailResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackFullResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackTypeResponse
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NearExistResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.NewStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.StoreDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NewServiceApi {

    // 리뷰
    @POST("/api/v2/store/review")
    suspend fun saveReview(
        @Body newReviewRequest: NewReviewRequest
    ): Response<NewReviewResponse>

    @GET("/api/v3/store/reviews/me")
    suspend fun getMyReviews(
        @Query("cursor") cursor: Int?,
        @Query("size") size: Int = 20,
    ): Response<MyReviewResponse>

    @PUT("/api/v2/store/review/{reviewId}")
    suspend fun editReview(
        @Path("reviewId") reviewId: Int,
        @Body editReviewRequest: EditReviewRequest
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

    @PUT("/api/v2/store/{storeId}")
    suspend fun editStore(
        @Path("storeId") storeId: Int,
        @Body editStoreRequest: NewStoreRequest
    ): Response<NewStoreResponse>

    @GET("/api/v1/stores/near/exists")
    suspend fun getNearExists(
        @Query("distance") distance: Double = 10.0,
        @Query("mapLatitude") latitude: Double,
        @Query("mapLongitude") longitude: Double
    ): Response<NearExistResponse>

    // 가게 검색
    @GET("/api/v2/store")
    suspend fun getStoreInfo(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("storeId") storeId: Int,
        @Query("startDate") startDate: String?
    ): Response<StoreDetailResponse>

    @GET("/api/v2/stores/near")
    suspend fun getNearStore(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("mapLatitude") mapLatitude: Double,
        @Query("mapLongitude") mapLongitude: Double,
        @Query("distance") distance: Double = 100000.0,
        @Query("orderType") orderType: String = Constants.DISTANCE_ASC,
        @Query("category") category: String = ""
    ): Response<NearStoreResponse>

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

    @GET("/api/v4/store/categories")
    suspend fun getCategories(): Response<CategoriesResponse>

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
        @Query("longitude") longitude: Double
    ): Response<BossStoreDetailResponse>

    @GET("/api/v1/boss/store/{bossStoreId}/feedbacks/full")
    suspend fun getBossStoreFeedbackFull(
        @Path("bossStoreId") bossStoreId: String
    ): Response<BossStoreFeedbackFullResponse>

    @GET("/api/v1/boss/store/feedback/types")
    suspend fun getBossStoreFeedbackType(): Response<BossStoreFeedbackTypeResponse>

    @POST("/api/v1/boss/store/{bossStoreId}/feedback")
    suspend fun postBossStoreFeedback(
        @Path("bossStoreId") bossStoreId: String,
        @Body feedbackTypes: BossStoreFeedbackRequest
    ): Response<BaseResponse<String>>

    @POST("/api/v1/device")
    suspend fun postPushInformation(@Body informationRequest: PushInformationRequest): Response<BaseResponse<String>>

    @PATCH("/api/v4/my/user-settings")
    suspend fun patchPushInformation(@Body patchPushInformationRequest: PatchPushInformationRequest): Response<BaseResponse<String>>

    @DELETE("/api/v1/device")
    suspend fun deletePushInformation(): Response<BaseResponse<String>>

    @PUT("/api/v1/device/token")
    suspend fun putPushInformationToken(@Body informationTokenRequest: PushInformationTokenRequest): Response<BaseResponse<String>>

    @GET("/api/v2/my/favorite-stores")
    suspend fun getMyFavoriteFolder(
        @Query("cursor") cursor: String?,
        @Query("size") size: Int
    ): Response<BaseResponse<MyFavoriteFolderResponse>>

    @GET("/api/v2/folder/{folderId}/favorite-stores")
    suspend fun getFavoriteViewer(
        @Path("folderId") id: String,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<MyFavoriteFolderResponse>>

    @POST("/api/v1/event/click/{targetType}/{targetId}")
    suspend fun eventClick(@Path("targetType") targetType: String, @Path("targetId") targetId: String): Response<BaseResponse<String>>

    @DELETE("/api/v2/my/favorite-stores")
    suspend fun allDeleteFavorite(): Response<BaseResponse<String>>

    @PUT("/api/v2/{favoriteType}/folder")
    suspend fun updateFavoriteInfo(
        @Path("favoriteType") favoriteType: String = FAVORITE_STORE,
        @Body favoriteInfoRequest: FavoriteInfoRequest
    ): Response<BaseResponse<String>>


}