package com.threedollar.data.login.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.ResultWrapper
import com.threedollar.common.base.map
import com.threedollar.common.base.toResultWrapper
import com.threedollar.data.login.datasource.LoginDataSource
import com.threedollar.data.login.datasource.LoginRemoteDataSource
import com.threedollar.data.login.mapper.FeedbackTypeMapper
import com.threedollar.data.login.mapper.SignUserMapper
import com.threedollar.domain.login.data.AccessCheckModel
import com.threedollar.domain.login.model.FeedbackTypeModel
import com.threedollar.domain.login.model.SignUserModel
import com.threedollar.domain.login.repository.LoginRepository
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val loginDataSource: LoginDataSource
) : LoginRepository {
    override fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeModel>>> =
        loginRemoteDataSource.getFeedbackTypes(targetType).map { response ->
            BaseResponse(
                ok = response.ok,
                data = response.data?.let { FeedbackTypeMapper.toDomainModelList(it) },
                message = response.message,
                resultCode = response.resultCode,
                error = response.error
            )
        }

    override fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>> =
        loginRemoteDataSource.putMarketingConsent(marketingConsent)

    override fun putPushInformation(pushToken: String): Flow<BaseResponse<String>> = loginRemoteDataSource.putPushInformation(pushToken)

    override fun getUserInfo(): Flow<AccessCheckModel> = loginRemoteDataSource.getUserInfo().map {
        AccessCheckModel(
            ok = it.ok,
            message = it.message,
            resultCode = it.resultCode
        )
    }

    override suspend fun signUp(name: String, socialType: String, token: String): ResultWrapper<SignUserModel?> =
        loginDataSource.signUp(SignUpRequest(name = name, socialType = socialType, token = token))
            .toResultWrapper()
            .map { it?.let(SignUserMapper::toDomainModel) }

    override suspend fun login(socialType: String, token: String): ResultWrapper<SignUserModel?> =
        loginDataSource.login(LoginRequest(socialType = socialType, token = token))
            .toResultWrapper()
            .map { it?.let(SignUserMapper::toDomainModel) }

    override suspend fun logout(): ResultWrapper<String?> = loginDataSource.logout().toResultWrapper()

    override suspend fun signOut(): ResultWrapper<String?> = loginDataSource.signOut().toResultWrapper()

    override suspend fun registerPushToken(pushToken: String): ResultWrapper<String?> =
        loginDataSource.putPushInformation(PushInformationRequest(pushToken = pushToken)).toResultWrapper()
}
