package com.zion830.threedollars.repository

import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.network.ServiceApi
import com.zion830.threedollars.repository.model.request.NewUser
import com.zion830.threedollars.repository.model.response.LoginResponse
import com.zion830.threedollars.utils.SharedPrefUtils

class UserRepository(
    private val service: ServiceApi = RetrofitBuilder.service
) {

    suspend fun tryLogin(nickName: String): LoginResponse {
        val newUser = NewUser(nickName, SharedPrefUtils.getKakaoId())
        return service.tryLogin(newUser)
    }
}