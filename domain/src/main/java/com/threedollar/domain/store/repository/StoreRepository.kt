package com.threedollar.domain.store.repository

import com.threedollar.network.sdui.model.screen.SDScreenModel

interface StoreRepository {
    suspend fun getScreenStore(
        storeId: Int,
        lat: Double,
        lng: Double
    ): Result<SDScreenModel>
}
