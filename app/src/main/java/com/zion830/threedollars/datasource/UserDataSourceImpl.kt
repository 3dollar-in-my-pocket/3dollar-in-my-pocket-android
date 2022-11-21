package com.zion830.threedollars.datasource

import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.network.NewServiceApi
import com.zion830.threedollars.datasource.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.FAQCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.my.*
import com.zion830.threedollars.datasource.model.v2.response.visit_history.MyVisitHistoryResponse
import retrofit2.Response
import zion830.com.common.base.BaseResponse
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(private val service: NewServiceApi) : UserDataSource {

    override suspend fun signOut(): Response<BaseResponse<String>> {
        return service.signOut()
    }

    override suspend fun signUp(signUpRequest: SignUpRequest): Response<SignResponse> {
        return service.signUp(signUpRequest)
    }

    override suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>> {
        return service.login(loginRequest)
    }

    override suspend fun logout(): Response<BaseResponse<String>> {
        return service.logout()
    }

    override suspend fun getMyInfo(): Response<MyInfoResponse> {
        return service.getMyInfo()
    }

    override suspend fun updateName(nickname: String): Response<MyInfoResponse> {
        return service.editNickname(EditNameRequest(nickname))
    }

    override suspend fun checkName(nickname: String): Response<BaseResponse<String>> {
        return service.checkNickname(nickname)
    }

    override suspend fun getMyReviews(cursor: Int?, size: Int): Response<MyReviewResponse> {
        return service.getMyReviews(cursor, size)
    }

    override suspend fun getMyStore(cursor: Int?, size: Int): Response<MyStoreResponse> {
        return service.getMyStore(cursor, size)
    }

    override suspend fun getMyVisitHistory(
        cursor: Int?,
        size: Int
    ): Response<MyVisitHistoryResponse> {
        return service.getMyVisitHistory(cursor, size)
    }

    override suspend fun getFAQCategory(): Response<FAQCategoryResponse> = service.getFAQCategory()

    override suspend fun getFAQList(category: String): Response<FAQByCategoryResponse> =
        service.getFAQByCategory(category)

    override suspend fun getMedals(): Response<BaseResponse<List<Medal>>> = service.getMedals()

    override suspend fun getUserActivity(): Response<UserActivityResponse> =
        service.getUserActivity()

    override suspend fun getMyMedals(): Response<BaseResponse<List<Medal>>> = service.getMyMedals()

    override suspend fun updateMyMedals(updateMedalRequest: UpdateMedalRequest): Response<BaseResponse<User>> =
        service.updateMyMedals(updateMedalRequest)

    override suspend fun postPushInformation(informationRequest: PushInformationRequest): Response<BaseResponse<String>> =
        service.postPushInformation(informationRequest)

    override suspend fun deletePushInformation(): Response<BaseResponse<String>> =
        service.deletePushInformation()

    override suspend fun putPushInformationToken(informationTokenRequest: PushInformationTokenRequest): Response<BaseResponse<String>> =
        service.putPushInformationToken(informationTokenRequest)
}