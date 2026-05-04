package com.threedollar.domain.screen.repository

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.HomeFilterScreenModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import kotlinx.coroutines.flow.Flow

interface ScreenRepository {
    fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<SDScreenModel>>

    fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<SDSectionModel.CardsSection>>

    fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenModel>>
}
