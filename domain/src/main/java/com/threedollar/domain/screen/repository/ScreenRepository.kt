package com.threedollar.domain.screen.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.HomeFilterScreenModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
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

    fun getHomeFilterScreen(preset: String? = null): Flow<BaseResponse<HomeFilterScreenModel>>

    fun getHomeCurationSection(
        tabId: String,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double? = null,
        deviceLongitude: Double? = null,
    ): Flow<BaseResponse<SDHomeCurationSectionModel>>

    fun getHomeCurationCards(
        tabId: String,
        carouselId: String,
        categoryId: String,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double? = null,
        deviceLongitude: Double? = null,
    ): Flow<BaseResponse<SDHomeCurationCardsModel>>
}
