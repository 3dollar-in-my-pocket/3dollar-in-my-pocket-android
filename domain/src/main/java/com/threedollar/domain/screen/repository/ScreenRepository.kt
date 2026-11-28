package com.threedollar.domain.screen.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.BottomNavigationTabModel
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

    fun getHomeNewsSection(newsTabId: String): Flow<BaseResponse<SDHomeNewsSectionModel>>

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

    /** 하단 탭 목록. 응답 순서 그대로이며, 화면 매핑·제외 규칙은 앱이 적용한다. */
    fun getBottomNavigation(): Flow<BaseResponse<List<BottomNavigationTabModel>>>
}
