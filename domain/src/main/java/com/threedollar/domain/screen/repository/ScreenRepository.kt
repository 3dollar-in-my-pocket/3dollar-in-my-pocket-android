package com.threedollar.domain.screen.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.HomeFilterScreenModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsSectionModel
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

    fun getHomeFilterScreen(preset: String? = null, tabId: String? = null): Flow<BaseResponse<HomeFilterScreenModel>>

    fun getHomeCurationSection(
        tabId: String,
        mapLatitude: Double,
        mapLongitude: Double,
        maxDistanceM: Double? = null,
        deviceLatitude: Double? = null,
        deviceLongitude: Double? = null,
    ): Flow<BaseResponse<SDHomeCurationSectionModel>>

    /** 홈 동네 소식 섹션. 서버가 지도 좌표 없이는 400 을 주므로 좌표는 필수다. 첫 페이지는 [cursor] 를 생략한다. */
    fun getHomeNewsSection(
        newsTabId: String,
        mapLatitude: Double,
        mapLongitude: Double,
        maxDistanceM: Double? = null,
        cursor: String? = null,
        deviceLatitude: Double? = null,
        deviceLongitude: Double? = null,
    ): Flow<BaseResponse<SDHomeNewsSectionModel>>

    fun getHomeCurationCards(
        tabId: String,
        carouselId: String,
        categoryId: String,
        mapLatitude: Double,
        mapLongitude: Double,
        maxDistanceM: Double? = null,
        deviceLatitude: Double? = null,
        deviceLongitude: Double? = null,
    ): Flow<BaseResponse<SDHomeCurationCardsModel>>
}
