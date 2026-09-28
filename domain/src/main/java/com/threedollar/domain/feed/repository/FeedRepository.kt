package com.threedollar.domain.feed.repository

import com.threedollar.domain.feed.model.FeedPageModel

interface FeedRepository {
    /**
     * 우리 동네 소식(`LOCAL_NEWS`) 피드. [cursor]가 null 이면 첫 페이지.
     * 좌표가 null 이면 파라미터를 생략하는데, 서버는 지도 좌표가 없으면 `missing_map_location_parameter` 로 실패한다.
     */
    suspend fun getLocalNewsFeeds(
        cursor: String?,
        mapLatitude: Double?,
        mapLongitude: Double?,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Result<FeedPageModel>
}
