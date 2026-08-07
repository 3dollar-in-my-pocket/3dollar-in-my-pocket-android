package com.threedollar.data.store.repository

import com.threedollar.domain.store.repository.StoreRepository
import com.threedollar.network.api.StoreApi
import com.threedollar.network.sdui.model.screen.SDScreenModel
import com.threedollar.network.util.runApi
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: StoreApi
) : StoreRepository {
    override suspend fun getScreenStore(
        storeId: Int,
        lat: Double,
        lng: Double
    ): Result<SDScreenModel> = runApi {
        storeApi.getScreenStore(
            storeId = storeId,
            lat = lat,
            lng = lng
        )
    }
}
