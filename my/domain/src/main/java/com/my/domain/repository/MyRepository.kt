package com.my.domain.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.data.poll.response.GetMyPollListResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.data.visit_history.MyVisitHistoryResponseV2
import com.threedollar.network.request.PatchUserInfoRequest
import kotlinx.coroutines.flow.Flow

interface MyRepository {

    fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>>
    fun patchUserInfo(patchUserInfoRequest: PatchUserInfoRequest): Flow<BaseResponse<String>>
    fun getMyFavoriteStores(size: Int = 20): Flow<BaseResponse<MyFavoriteFolderResponse>>
    fun getMyVisitsStore(size: Int = 20): Flow<BaseResponse<MyVisitHistoryResponseV2>>
    fun getUserPollList(cursor: Int?, size: Int = 20): Flow<BaseResponse<GetMyPollListResponse>>

}