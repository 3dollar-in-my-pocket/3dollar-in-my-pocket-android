package com.my.domain.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import kotlinx.coroutines.flow.Flow

interface MyRepository {

    fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>>

}