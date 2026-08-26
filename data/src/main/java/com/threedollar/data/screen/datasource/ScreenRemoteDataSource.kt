package com.threedollar.data.screen.datasource

import com.google.gson.JsonElement
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import kotlinx.coroutines.flow.Flow

interface ScreenRemoteDataSource {
    fun getHomeListSection(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
        dynamicParams: Map<String, String>,
        cursor: String?,
    ): Flow<BaseResponse<HomeListSectionResponse>>

    fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<StoreContributorScreenResponse>>

    fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<StoreContributorHistoriesResponse>>

    fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenResponse>>

    fun getStoreDetailScreen(
        storeId: Long,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Flow<BaseResponse<StoreDetailScreenResponse>>

    fun putStorePostStickers(
        storeId: Long,
        postId: Long,
        stickers: List<String>,
    ): Flow<BaseResponse<JsonElement>>

    fun issueStoreCoupon(storeId: Long, couponId: String): Flow<BaseResponse<JsonElement>>

    fun useIssuedCoupon(issuedKey: String): Flow<BaseResponse<JsonElement>>

    fun deleteStoreReview(reviewId: Long): Flow<BaseResponse<JsonElement>>
}
