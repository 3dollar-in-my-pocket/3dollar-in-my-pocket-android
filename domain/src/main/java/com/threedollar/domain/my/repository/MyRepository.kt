package com.threedollar.domain.my.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.domain.my.model.UserInfoModel
import com.threedollar.domain.my.model.FavoriteStoresModel
import com.threedollar.domain.my.model.VisitHistoryModel
import com.threedollar.domain.my.model.UserPollsModel
import com.threedollar.domain.my.model.UserInfoUpdateModel
import kotlinx.coroutines.flow.Flow

interface MyRepository {

    fun getUserInfo(): Flow<BaseResponse<UserInfoModel>>
    fun patchUserInfo(userInfoUpdate: UserInfoUpdateModel): Flow<BaseResponse<String>>
    fun getMyFavoriteStores(size: Int = 20): Flow<BaseResponse<FavoriteStoresModel>>
    fun getMyVisitsStore(size: Int = 20): Flow<BaseResponse<VisitHistoryModel>>
    fun getUserPollList(cursor: Int?, size: Int = 20): Flow<BaseResponse<UserPollsModel>>

}