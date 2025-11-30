package com.zion830.threedollars.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.request.PatchPushInformationRequest
import com.zion830.threedollars.datasource.model.v2.request.EditNameRequest
import com.zion830.threedollars.datasource.model.v2.request.FavoriteInfoRequest
import com.zion830.threedollars.datasource.model.v2.request.UpdateMedalRequest
import com.zion830.threedollars.datasource.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.FAQCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.my.Medal
import com.zion830.threedollars.datasource.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.datasource.model.v2.response.my.MyReviewResponse
import com.zion830.threedollars.datasource.model.v2.response.my.MyStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.my.User
import com.zion830.threedollars.datasource.model.v2.response.my.UserActivityResponse
import com.zion830.threedollars.network.NewServiceApi
import retrofit2.Response
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(private val service: NewServiceApi) : UserDataSource {

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

    override suspend fun getFAQCategory(): Response<FAQCategoryResponse> = service.getFAQCategory()

    override suspend fun getFAQList(category: String): Response<FAQByCategoryResponse> =
        service.getFAQByCategory(category)

    override suspend fun getMedals(): Response<BaseResponse<List<Medal>>> = service.getMedals()

    override suspend fun getUserActivity(): Response<UserActivityResponse> =
        service.getUserActivity()

    override suspend fun getMyMedals(): Response<BaseResponse<List<Medal>>> = service.getMyMedals()

    override suspend fun updateMyMedals(updateMedalRequest: UpdateMedalRequest): Response<BaseResponse<User>> =
        service.updateMyMedals(updateMedalRequest)


    override suspend fun patchPushInformation(patchPushInformationRequest: PatchPushInformationRequest): Response<BaseResponse<String>> =
        service.patchPushInformation(patchPushInformationRequest)

    override suspend fun deletePushInformation(): Response<BaseResponse<String>> =
        service.deletePushInformation()

    override suspend fun getFavoriteViewer(favoriteId: String, cursor: String?): Response<BaseResponse<MyFavoriteFolderResponse>> =
        service.getFavoriteViewer(favoriteId, cursor)

    override suspend fun getMyFavoriteFolder(cursor: String?, size: Int): Response<BaseResponse<MyFavoriteFolderResponse>> =
        service.getMyFavoriteFolder(cursor, size)

    override suspend fun eventClick(targetType: String, targetId: String): Response<BaseResponse<String>> =
        service.eventClick(targetType, targetId)

    override suspend fun allDeleteFavorite(): Response<BaseResponse<String>> =
        service.allDeleteFavorite()

    override suspend fun updateFavoriteInfo(favoriteInfoRequest: FavoriteInfoRequest): Response<BaseResponse<String>> =
        service.updateFavoriteInfo(favoriteInfoRequest = favoriteInfoRequest)
}