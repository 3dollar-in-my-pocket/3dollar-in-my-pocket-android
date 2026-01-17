package com.threedollar.data.login.repository

import com.threedollar.domain.login.model.SignUpModel
import com.threedollar.domain.login.model.SignUpName
import com.threedollar.domain.login.model.UserRandomNameModel
import com.threedollar.domain.login.repository.SignUpRepository
import com.threedollar.common.ext.toEmpty
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.api.LoginApi
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.util.runApi
import javax.inject.Inject

internal class SignUpRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi,
    private val prefUtils: SharedPrefUtils
) : SignUpRepository {

    override suspend fun getRandomName(): Result<UserRandomNameModel> = runApi {
        loginApi.postRandomName()
    }.map { ret ->
        UserRandomNameModel(
            name = ret.contents.firstOrNull { it.name.isNotEmpty() }?.name.orEmpty()
        )
    }

    override suspend fun checkName(name: String): Result<Unit> = runApi {
        loginApi.checkName(name)
    }.toEmpty()

    override suspend fun signUp(model: SignUpModel): Result<Unit> = runApi {
        when (model.name) {
            is SignUpName.Manual -> {
                loginApi.signUp(model.toRequest())
            }

            is SignUpName.Random -> {
                loginApi.signUpWithRandomName(model.toRequest())
            }
        }
    }.onSuccess {
        prefUtils.run {
            saveLoginType(model.token.socialType)
            saveUserId(it.userId)
            saveAccessToken(it.token)
        }
    }.toEmpty()

    private fun SignUpModel.toRequest() = SignUpRequest(
        name = name.value,
        socialType = token.socialType,
        token = token.value,
    )
}
