package com.my.domain.repository

import com.threedollar.common.base.BaseResponse
import com.my.domain.model.UserInfoModel
import com.my.domain.model.FavoriteStoresModel
import com.my.domain.model.VisitHistoryModel
import com.my.domain.model.UserPollsModel
import com.my.domain.model.UserInfoUpdateModel
import kotlinx.coroutines.flow.Flow

interface MyRepository {

    fun getUserInfo(): Flow<BaseResponse<UserInfoModel>>
    fun patchUserInfo(userInfoUpdate: UserInfoUpdateModel): Flow<BaseResponse<String>>
    fun getMyFavoriteStores(size: Int = 20): Flow<BaseResponse<FavoriteStoresModel>>
    fun getMyVisitsStore(size: Int = 20): Flow<BaseResponse<VisitHistoryModel>>
    fun getUserPollList(cursor: Int?, size: Int = 20): Flow<BaseResponse<UserPollsModel>>

}