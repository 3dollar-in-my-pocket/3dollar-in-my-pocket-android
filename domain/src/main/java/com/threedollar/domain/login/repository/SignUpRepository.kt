package com.threedollar.domain.login.repository

import com.threedollar.domain.login.model.SignUpModel
import com.threedollar.domain.login.model.UserRandomNameModel

interface SignUpRepository {
    suspend fun getRandomName(): Result<UserRandomNameModel>
    suspend fun checkName(name: String): Result<Unit>
    suspend fun signUp(model: SignUpModel): Result<Unit>
}
