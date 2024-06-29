package com.my.data.repository

import com.my.domain.repository.MyRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.data.poll.response.GetMyPollListResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.data.visit_history.MyVisitHistoryResponseV2
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(private val serverApi: ServerApi) : MyRepository {

    override fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>> = flow {
        emit(apiResult(serverApi.getUserInfo()))
    }

    override fun getMyFavoriteStores(size: Int): Flow<BaseResponse<MyFavoriteFolderResponse>> = flow {
        emit(apiResult(serverApi.getMyFavoriteStores(size)))
    }

    override fun getMyVisitsStore(size: Int): Flow<BaseResponse<MyVisitHistoryResponseV2>> = flow {
        emit(apiResult(serverApi.getMyVisitsStore(size)))
    }

    override fun getUserPollList(cursor: Int?, size: Int): Flow<BaseResponse<GetMyPollListResponse>> = flow {
        emit(apiResult(serverApi.getMyPollList(cursor, size)))
    }

}