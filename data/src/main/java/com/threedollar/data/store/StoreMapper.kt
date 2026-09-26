package com.threedollar.data.store

import com.threedollar.data.home.asModel
import com.threedollar.domain.home.data.store.LocationModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.threedollar.domain.store.model.SessionViewCountRangeModel
import com.threedollar.domain.store.model.StoreDisplayItemModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.model.StoreDisplayItemsModel
import com.threedollar.domain.store.model.StoreDisplayTriggerConditionsModel
import com.threedollar.domain.store.model.StoreDisplayTriggerModel
import com.threedollar.domain.store.model.StoreDisplayTriggerType
import com.threedollar.network.data.store.SessionViewCountRangeResponse
import com.threedollar.network.data.store.StoreDisplayItemResponse
import com.threedollar.network.data.store.StoreDisplayItemsResponse
import com.threedollar.network.data.store.StoreDisplayTriggerConditionsResponse
import com.threedollar.network.data.store.StoreDisplayTriggerResponse
import com.threedollar.network.data.store.StoreV5Response

fun StoreDisplayItemsResponse.asModel() = StoreDisplayItemsModel(
    contents = contents?.map { it.asModel() } ?: listOf(),
)

private fun StoreDisplayItemResponse.asModel() = StoreDisplayItemModel(
    itemType = StoreDisplayItemType.from(itemType),
    description = description ?: "",
    isVisible = isVisible ?: false,
    trigger = trigger?.asModel(),
)

private fun StoreDisplayTriggerResponse.asModel() = StoreDisplayTriggerModel(
    type = StoreDisplayTriggerType.from(type),
    displayAfterSeconds = displayAfterSeconds ?: 0.0,
    displayDurationSeconds = displayDurationSeconds,
    conditions = conditions?.asModel(),
)

private fun StoreDisplayTriggerConditionsResponse.asModel() = StoreDisplayTriggerConditionsModel(
    sessionViewCountRange = sessionViewCountRange?.asModel(),
)

private fun SessionViewCountRangeResponse.asModel() = SessionViewCountRangeModel(
    min = min,
    max = max,
)

fun StoreV5Response.asUserStoreModel() = UserStoreModel(
    storeId = storeId?.toIntOrNull() ?: 0,
    name = name.orEmpty(),
    location = location?.asModel() ?: LocationModel(),
    categories = categories?.map { it.asModel() }.orEmpty(),
)
