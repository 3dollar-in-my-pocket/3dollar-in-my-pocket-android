package com.threedollar.domain.login.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.ResultWrapper
import com.threedollar.domain.login.data.AccessCheckModel
import com.threedollar.domain.login.model.FeedbackTypeModel
import com.threedollar.domain.login.model.SignUserModel
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeModel>>>

    fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>>

    fun putPushInformation(pushToken: String): Flow<BaseResponse<String>>

    fun getUserInfo(): Flow<AccessCheckModel>

    suspend fun signUp(name: String, socialType: String, token: String): ResultWrapper<SignUserModel?>

    suspend fun login(socialType: String, token: String): ResultWrapper<SignUserModel?>

    suspend fun logout(): ResultWrapper<String?>

    suspend fun signOut(): ResultWrapper<String?>

    /**
     * 푸시 토큰을 서버에 등록하고, 성공하면 기기에 저장한다.
     *
     * [putPushInformation] 과 같은 서버 API 를 부르지만 이쪽만 성공 시 토큰을 로컬에 저장한다.
     */
    suspend fun registerPushToken(pushToken: String): ResultWrapper<String?>
}
