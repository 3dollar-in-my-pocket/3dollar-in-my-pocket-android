package com.zion830.threedollars.network

import com.zion830.threedollars.Constants
import com.zion830.threedollars.Constants.FAVORITE_STORE
import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.FAQCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.NewReviewResponse
import com.zion830.threedollars.datasource.model.v4.ad.AdResponse
import com.zion830.threedollars.datasource.model.v4.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.my.*
import com.zion830.threedollars.datasource.model.v2.response.store.*
import com.zion830.threedollars.datasource.model.v2.response.visit_history.MyVisitHistoryResponse
import com.zion830.threedollars.datasource.model.v4.aroundStore.AroundStoreResponse
import com.zion830.threedollars.datasource.model.v4.boss.BossStoreResponse
import com.zion830.threedollars.datasource.model.v4.categories.CategoriesResponse
import com.zion830.threedollars.datasource.model.v4.user.LoginResponse
import com.zion830.threedollars.datasource.model.v4.user.request.ConnectAccountRequest
import com.zion830.threedollars.datasource.model.v4.user.request.SignUpRequest
import com.zion830.threedollars.datasource.model.v4.device.PushInformationRequest
import com.zion830.threedollars.datasource.model.v4.districts.DistrictsResponse
import com.zion830.threedollars.datasource.model.v4.feedback.FeedbackCountResponse
import com.zion830.threedollars.datasource.model.v4.feedback.FeedbackTypeResponse
import com.zion830.threedollars.datasource.model.v4.feedback.UserStoreFeedbackResponse
import com.zion830.threedollars.datasource.model.v4.feedback.request.FeedbackTypes
import com.zion830.threedollars.datasource.model.v4.medal.MedalResponse
import com.zion830.threedollars.datasource.model.v4.medal.request.UpdateMedalRequest
import com.zion830.threedollars.datasource.model.v4.nearExists.NearExistResponse
import com.zion830.threedollars.datasource.model.v4.report.ReportReasonResponse
import com.zion830.threedollars.datasource.model.v4.user.UserActivityResponse
import com.zion830.threedollars.datasource.model.v4.user.UserInfoResponse
import com.zion830.threedollars.datasource.model.v4.user.request.EditNameRequest
import com.zion830.threedollars.datasource.model.v4.user.request.UserInfoRequest
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
    ): Response<BaseResponse<UserStoreFeedbackResponse>>

    // 대상에 피드백을 등록합니다.
    @POST("/api/v1/feedback/{targetType}/target/{targetId}")
    suspend fun postTargetFeedback(
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: String,
        @Body feedbackTypes: FeedbackTypes,
    ): Response<BaseResponse<String>>

    // 전체 기간동안의 대상에 등록된 피드백 갯수를 조회합니다.
    @GET("/api/v1/feedback/{targetType}/target/{targetId}/full")
    suspend fun getTargetFeedbackCount(
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: String,
    ): Response<BaseResponse<List<FeedbackCountResponse>>>

    // 대상에 등록할 수 있는 피드백 종류를 조회합니다.
    @GET("/api/v1/feedback/{targetType}/types")
    suspend fun getTargetTypeFeedback(
        @Path("targetType") targetType: String,
    ): Response<BaseResponse<List<FeedbackTypeResponse>>>

    // User-Agent에서 디바이스 OS 정보를 읽어서 타게팅 광고를 노출 합니다.
    @GET("/api/v1/advertisements")
    suspend fun getAds(
        @Query("position") position: String,
        @Query("size") size: Int? = null,
    ): Response<BaseResponse<List<AdResponse>>>

    // 사장님 가게를 조회합니다.
    @GET("/api/v4/boss-store/{bossStoreId}")
    suspend fun getSpecificBossStore(
        @Path("bossStoreId") bossStoreId: String,
        @Header("X-Device-Latitude") deviceLatitude: Double,
        @Header("X-Device-Longitude") deviceLongitude: Double,
    ): Response<BaseResponse<BossStoreResponse>>

    // 신고 이유 목록을 조회합니다.
    @GET("/api/v1/group/{group}/report/reasons")
    suspend fun getReportReasons(
        @Path("group") group: String,
    ): Response<BaseResponse<ReportReasonResponse>>

    // 유저의 마케팅 활용 여부를 수정합니다.
    @PUT("/api/v1/user/me/marketing-consent")
    suspend fun putMarketingConsent(@Body marketingConsentRequest: MarketingConsentRequest): Response<BaseResponse<String>>

    // 유저의 계정 정보를 수정합니다. (일부분만 업데이트시)
    // 중복된 닉네임의 경우 409, 사용 불가능한 닉네임의 경우 400을 반환
    @PATCH("/api/v2/user")
    suspend fun patchUserInfo(@Body userInfoRequest: UserInfoRequest): Response<BaseResponse<String>>

    // 유저의 계정 정보를 조회합니다.
    @GET("/api/v2/user/me")
    suspend fun getUserInfo(): Response<BaseResponse<UserInfoResponse>>

    // 유저의 닉네임 정보를 수정합니다.
    @PUT("/api/v2/user/me")
    suspend fun editNickname(@Body editNameRequest: EditNameRequest): Response<BaseResponse<UserInfoResponse>>

    // 유저가 사용할 수 있는 닉네임인지 확인합니다.
    // 중복된 닉네임의 경우 409, 사용 불가능한 닉네임의 경우 400을 반환
    @GET("/api/v2/user/name/available")
    suspend fun checkNickname(@Query("name") name: String): Response<BaseResponse<String>>

    // 유저 계정에 디바이스를 등록합니다.
    @POST("/api/v1/device")
    suspend fun postPushInformation(@Body informationRequest: PushInformationRequest): Response<BaseResponse<String>>

    // 유저 계정의 디바이스 정보를 삭제합니다.
    @DELETE("/api/v1/device")
    suspend fun deletePushInformation(): Response<BaseResponse<String>>

    // 유저 계정에 등록된 디바이스의 토큰을 갱신합니다. (해당 유저에 등록된 디바이스가 없는 경우 별도의 갱신 없이 200 OK가 반환됩니다.)
    @PUT("/api/v1/device/token")
    suspend fun putPushInformationToken(@Body informationTokenRequest: PushInformationRequest): Response<BaseResponse<String>>

    // 전체 메달 목록을 조회합니다.
    @GET("/api/v1/medals")
    suspend fun getMedals(): Response<BaseResponse<List<MedalResponse>>>

    // 유저가 장착중인 메달을 수정한다.
    @PUT("/api/v1/user/medal")
    suspend fun updateMyMedals(@Body updateMedalRequest: UpdateMedalRequest): Response<BaseResponse<UserInfoResponse>>

    // 유저가 보유중인 메달들을 조회한다.
    @GET("/api/v1/user/medals")
    suspend fun getMyMedals(): Response<BaseResponse<List<MedalResponse>>>

    // 익명 계정에 소셜 계정을 연결합니다.
    @PUT("/api/v1/connect/account")
    suspend fun putConnectAccount(
        @Body connectAccountRequest: ConnectAccountRequest,
    ): Response<BaseResponse<String>>

    // 익명으로 회원가입을 요청합니다.
    @POST("/api/v1/signup/anonymous")
    suspend fun postSignUpAnonymous(): Response<BaseResponse<LoginResponse>>

    // 로그인을 요청합니다.
    @POST("/api/v2/login")
    suspend fun login(@Body connectAccountRequest: ConnectAccountRequest): Response<BaseResponse<LoginResponse>>

    // 로그아웃을 요청합니다.
    @POST("/api/v2/logout")
    suspend fun logout(): Response<BaseResponse<String>>

    // 회원탈퇴를 요청합니다.
    @DELETE("/api/v2/signout")
    suspend fun signOut(): Response<BaseResponse<String>>

    // 회원가입을 요청합니다.
    @POST("/api/v2/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<BaseResponse<LoginResponse>>

    // 유저의 활동 정보를 조회합니다.
    @GET("/api/v1/user/activity")
    suspend fun getUserActivity() : Response<BaseResponse<UserActivityResponse>>

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

    @GET("/api/v2/store/visits/me")
    suspend fun getMyVisitHistory(
        @Query("cursor") cursor: Int?,
        @Query("size") size: Int,
    ): Response<MyVisitHistoryResponse>

    // faq
    @GET("/api/v2/faq/categories")
    suspend fun getFAQCategory(): Response<FAQCategoryResponse>

    @GET("/api/v2/faqs")
    suspend fun getFAQByCategory(@Query("category") category: String): Response<FAQByCategoryResponse>

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