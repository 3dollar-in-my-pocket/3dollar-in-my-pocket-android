package com.threedollar.domain.screen.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.HomeFilterScreenModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import kotlinx.coroutines.flow.Flow

interface ScreenRepository {
    fun getHomeListSection(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
        dynamicParams: Map<String, String> = emptyMap(),
        cursor: String? = null,
    ): Flow<BaseResponse<HomeListSectionModel>>

    fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<SDScreenModel>>

    fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<SDSectionModel.CardsSection>>

    fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenModel>>

    fun getStoreDetailScreen(
        storeId: Long,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Flow<BaseResponse<StoreDetailScreenModel>>

    fun putStorePostStickers(storeId: Long, postId: Long, stickers: List<String>): Flow<BaseResponse<Boolean>>

    fun issueStoreCoupon(storeId: Long, couponId: String): Flow<BaseResponse<Boolean>>

    fun useIssuedCoupon(issuedKey: String): Flow<BaseResponse<Boolean>>

    fun deleteStoreReview(reviewId: Long): Flow<BaseResponse<Boolean>>
}
