package com.threedollar.data.store.repository

import com.threedollar.data.store.asModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.model.StoreDisplayItemsModel
import com.threedollar.domain.store.repository.StoreRepository
import com.threedollar.network.api.StoreApi
import com.threedollar.network.request.StoreDisplayItemsRequest
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

    override suspend fun getStoreDisplayItems(
        storeId: Int,
        lat: Double,
        lng: Double,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<StoreDisplayItemsModel> = runApi {
        storeApi.getStoreDisplayItems(
            storeId = storeId,
            itemTypes = itemTypes.map { it.value },
            lat = lat,
            lng = lng,
        )
    }.map { it.asModel() }

    override suspend fun postStoreDisplayItemImpression(
        storeId: Int,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<String> = runApi {
        storeApi.postStoreDisplayItemImpression(
            storeId = storeId,
            request = StoreDisplayItemsRequest(itemTypes.map { it.value }),
        )
    }
}
