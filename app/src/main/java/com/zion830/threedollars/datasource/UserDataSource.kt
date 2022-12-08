package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.FAQCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.my.*
import com.zion830.threedollars.datasource.model.v2.response.visit_history.MyVisitHistoryResponse
import retrofit2.Response
import zion830.com.common.base.BaseResponse

interface UserDataSource {

    suspend fun signOut(): Response<BaseResponse<String>>

    suspend fun signUp(signUpRequest: SignUpRequest): Response<SignResponse>

    suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>>

    suspend fun logout(): Response<BaseResponse<String>>

    suspend fun getMyInfo(): Response<MyInfoResponse>

    suspend fun updateName(nickname: String): Response<MyInfoResponse>

    suspend fun checkName(nickname: String): Response<BaseResponse<String>>

    suspend fun getMyReviews(cursor: Int?, size: Int): Response<MyReviewResponse>

    suspend fun getMyStore(cursor: Int?, size: Int): Response<MyStoreResponse>

    suspend fun getMyVisitHistory(cursor: Int?, size: Int): Response<MyVisitHistoryResponse>

    suspend fun getFAQCategory(): Response<FAQCategoryResponse>

    suspend fun getFAQList(category: String): Response<FAQByCategoryResponse>

    suspend fun getMedals(): Response<BaseResponse<List<Medal>>>

    suspend fun getUserActivity(): Response<UserActivityResponse>

    suspend fun getMyMedals(): Response<BaseResponse<List<Medal>>>

    suspend fun updateMyMedals(updateMedalRequest: UpdateMedalRequest): Response<BaseResponse<User>>

    suspend fun postPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>>

    suspend fun deletePushInformation(): Response<BaseResponse<String>>

    suspend fun putPushInformationToken(informationTokenRequest: PushInformationTokenRequest): Response<BaseResponse<String>>

    suspend fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest): Response<BaseResponse<String>>

    suspend fun getMyFavoriteFolder(cursor: Int?, size: Int): Response<BaseResponse<MyFavoriteFolderResponse>>

    suspend fun eventClick(targetType: String, targetId: String): Response<BaseResponse<String>>

    suspend fun allDeleteFavorite(): Response<BaseResponse<String>>

    suspend fun deleteFavorite(storeType: String, storeId: String): Response<BaseResponse<String>>

    suspend fun updateFavoriteInfo(favoriteInfoRequest : FavoriteInfoRequest) : Response<BaseResponse<String>>
}