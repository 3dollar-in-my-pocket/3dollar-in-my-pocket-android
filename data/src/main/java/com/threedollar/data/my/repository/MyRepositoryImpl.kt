package com.threedollar.data.my.repository

import com.threedollar.domain.my.repository.MyRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.domain.my.model.UserInfoModel
import com.threedollar.domain.my.model.FavoriteStoresModel
import com.threedollar.domain.my.model.VisitHistoryModel
import com.threedollar.domain.my.model.UserPollsModel
import com.threedollar.domain.my.model.UserInfoUpdateModel
import com.threedollar.data.my.mapper.UserInfoMapper
import com.threedollar.data.my.mapper.FavoriteStoresMapper
import com.threedollar.data.my.mapper.VisitHistoryMapper
import com.threedollar.data.my.mapper.UserPollsMapper
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(private val serverApi: ServerApi) : MyRepository {

    override fun getUserInfo(): Flow<BaseResponse<UserInfoModel>> = flow {
        val response = apiResult(serverApi.getUserInfo())
        emit(BaseResponse(
            ok = response.ok,
            data = response.data?.let { UserInfoMapper.toDomainModel(it) },
            message = response.message,
            resultCode = response.resultCode,
            error = response.error
        ))
    }

    override fun patchUserInfo(userInfoUpdate: UserInfoUpdateModel): Flow<BaseResponse<String>> = flow {
        val request = UserInfoMapper.toNetworkRequest(userInfoUpdate)
        emit(apiResult(serverApi.patchUserInfo(request)))
    }

    override fun getMyFavoriteStores(size: Int): Flow<BaseResponse<FavoriteStoresModel>> = flow {
        val response = apiResult(serverApi.getMyFavoriteStores(size))
        emit(BaseResponse(
            ok = response.ok,
            data = response.data?.let { FavoriteStoresMapper.toDomainModel(it) },
            message = response.message,
            resultCode = response.resultCode,
            error = response.error
        ))
    }

    override fun getMyVisitsStore(size: Int): Flow<BaseResponse<VisitHistoryModel>> = flow {
        val response = apiResult(serverApi.getMyVisitsStore(size))
        emit(BaseResponse(
            ok = response.ok,
            data = response.data?.let { VisitHistoryMapper.toDomainModel(it) },
            message = response.message,
            resultCode = response.resultCode,
            error = response.error
        ))
    }

    override fun getUserPollList(cursor: Int?, size: Int): Flow<BaseResponse<UserPollsModel>> = flow {
        val response = apiResult(serverApi.getMyPollList(cursor, size))
        emit(BaseResponse(
            ok = response.ok,
            data = response.data?.let { UserPollsMapper.toDomainModel(it) },
            message = response.message,
            resultCode = response.resultCode,
            error = response.error
        ))
    }

}