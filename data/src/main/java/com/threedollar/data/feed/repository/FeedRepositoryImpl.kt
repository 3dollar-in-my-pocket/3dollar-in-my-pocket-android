package com.threedollar.data.feed.repository

import com.threedollar.data.feed.asModel
import com.threedollar.domain.feed.model.FeedPageModel
import com.threedollar.domain.feed.repository.FeedRepository
import com.threedollar.network.api.FeedApi
import com.threedollar.network.util.runApi
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedApi: FeedApi,
) : FeedRepository {

    override suspend fun getLocalNewsFeeds(
        cursor: String?,
        mapLatitude: Double?,
        mapLongitude: Double?,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Result<FeedPageModel> = runApi {
        feedApi.getFeeds(
            ticketId = LOCAL_NEWS_TICKET_ID,
            cursor = cursor,
            size = PAGE_SIZE,
            mapLatitude = mapLatitude,
            mapLongitude = mapLongitude,
            deviceLatitude = deviceLatitude,
            deviceLongitude = deviceLongitude,
        )
    }.map { it.asModel() }

    private companion object {
        const val LOCAL_NEWS_TICKET_ID = "LOCAL_NEWS"
        const val PAGE_SIZE = 20
    }
}
