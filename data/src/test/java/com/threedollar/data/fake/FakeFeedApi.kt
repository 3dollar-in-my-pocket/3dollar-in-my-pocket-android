package com.threedollar.data.fake

import com.google.gson.Gson
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.FeedApi
import com.threedollar.network.data.feed.FeedListResponse
import retrofit2.Response

/** 피드 응답만 주입할 수 있는 [FeedApi] 대역. 요청 파라미터는 기록해 둔다. */
class FakeFeedApi : FeedApi {

    var response: Response<BaseResponse<FeedListResponse>> =
        Response.success(BaseResponse(ok = true, data = FeedListResponse()))

    val requests = mutableListOf<Request>()

    fun givenFixture(path: String) {
        val json = requireNotNull(javaClass.classLoader?.getResource(path)).readText()
        response = Response.success(BaseResponse(ok = true, data = Gson().fromJson(json, FeedListResponse::class.java)))
    }

    override suspend fun getFeeds(
        ticketId: String,
        cursor: String?,
        size: Int,
        mapLatitude: Double?,
        mapLongitude: Double?,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Response<BaseResponse<FeedListResponse>> {
        requests += Request(ticketId, cursor, size, mapLatitude, mapLongitude)
        return response
    }

    data class Request(
        val ticketId: String,
        val cursor: String?,
        val size: Int,
        val mapLatitude: Double?,
        val mapLongitude: Double?,
    )
}
