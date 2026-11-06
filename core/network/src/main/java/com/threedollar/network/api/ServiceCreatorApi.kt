package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.creator.ContentListCreatorGroupResponse
import retrofit2.Response
import retrofit2.http.GET

interface ServiceCreatorApi {
    @GET("/api/v1/service-creators")
    suspend fun listCreators(): Response<BaseResponse<ContentListCreatorGroupResponse>>
}
