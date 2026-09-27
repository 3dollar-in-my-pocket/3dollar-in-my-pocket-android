package com.threedollar.network.api

import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.feed.FeedListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedApi {
    /** 지역 피드 등 피드 티켓별 카드 목록. 지도 좌표가 없으면 서버가 `missing_map_location_parameter` 로 실패한다. */
    @GET("/api/v1/feed-ticket/{ticketId}/feeds")
    suspend fun getFeeds(
        @Path("ticketId") ticketId: String,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
        @Query("mapLatitude") mapLatitude: Double?,
        @Query("mapLongitude") mapLongitude: Double?,
        @Header("X-Device-Latitude") deviceLatitude: Double?,
        @Header("X-Device-Longitude") deviceLongitude: Double?,
    ): Response<BaseResponse<FeedListResponse>>
}
