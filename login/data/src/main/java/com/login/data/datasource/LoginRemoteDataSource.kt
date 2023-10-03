package com.login.data.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.store.BossStoreResponse
import com.threedollar.network.data.user.UserResponse
import com.threedollar.network.request.MarketingConsentRequest
import com.threedollar.network.request.PushInformationRequest
import kotlinx.coroutines.flow.Flow

interface LoginRemoteDataSource {

    fun getFeedbackTypes(targetType: String): Flow<BaseResponse<List<FeedbackTypeResponse>>>

}