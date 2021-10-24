package com.zion830.threedollars.repository

import com.zion830.threedollars.network.NewServiceApi
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.v2.request.EditNameRequest
import com.zion830.threedollars.repository.model.v2.request.LoginRequest
import com.zion830.threedollars.repository.model.v2.request.SignUpRequest
import com.zion830.threedollars.repository.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.repository.model.v2.response.FAQCategoryResponse
import com.zion830.threedollars.repository.model.v2.response.my.*
import retrofit2.Response
import zion830.com.common.base.BaseResponse

class UserRepository(
    private val service: NewServiceApi = RetrofitBuilder.newServiceApi
) {

    suspend fun signout(): Response<BaseResponse<String>> {
        return service.signOut()
    }

    suspend fun signUp(signUpRequest: SignUpRequest): Response<SignResponse> {
        return service.signUp(signUpRequest)
    }

    suspend fun login(loginRequest: LoginRequest): Response<BaseResponse<SignUser>> {
        return service.login(loginRequest)
    }

    suspend fun logout(): Response<BaseResponse<String>> {
        return service.logout()
    }

    suspend fun getMyInfo(): Response<MyInfoResponse> {
        return service.getMyInfo()
    }

    suspend fun updateName(nickname: String): Response<MyInfoResponse> {
        return service.editNickname(EditNameRequest(nickname))
    }

    suspend fun checkName(nickname: String): Response<BaseResponse<String>> {
        return service.checkNickname(nickname)
    }

    suspend fun getMyReviews(cursor: Int): Response<MyReviewResponse> {
        return service.getMyReviews()
    }

    suspend fun getMyStore(latitude: Double, longitude: Double, cursor: Int): Response<MyStoreResponse> {
        return service.getMyStore(latitude, longitude)
    }

    suspend fun getFAQCategory(): Response<FAQCategoryResponse> = service.getFAQCategory()

    suspend fun getFAQList(category: String): Response<FAQByCategoryResponse> = service.getFAQByCategory(category)
}