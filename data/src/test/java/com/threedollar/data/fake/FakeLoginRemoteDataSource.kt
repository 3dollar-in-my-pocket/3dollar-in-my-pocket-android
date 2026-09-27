package com.threedollar.data.fake

import com.threedollar.common.base.BaseResponse
import com.threedollar.data.login.datasource.LoginRemoteDataSource
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import kotlinx.coroutines.flow.Flow

/**
 * [LoginRemoteDataSource] 테스트 대역. 아직 스텁이 필요한 테스트가 없어 모든 메서드가 호출되면 실패한다.
 */
object FakeLoginRemoteDataSource : LoginRemoteDataSource {
    override fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>> = error("사용하지 않음")
    override fun putMarketingConsent(marketingConsent: String): Flow<BaseResponse<String>> = error("사용하지 않음")
    override fun putPushInformation(pushToken: String): Flow<BaseResponse<String>> = error("사용하지 않음")
    override fun getUserInfo(): Flow<BaseResponse<UserWithDetailApiResponse>> = error("사용하지 않음")
}
