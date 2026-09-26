package com.threedollar.domain.store.repository

import com.threedollar.common.sdui.model.screen.SDScreenModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.model.StoreDisplayItemsModel

interface StoreRepository {
    suspend fun getScreenStore(
        storeId: Int,
        lat: Double,
        lng: Double
    ): Result<SDScreenModel>

    suspend fun getStoreDisplayItems(
        storeId: Int,
        lat: Double,
        lng: Double,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<StoreDisplayItemsModel>

    suspend fun postStoreDisplayItemImpression(
        storeId: Int,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<String>
}
