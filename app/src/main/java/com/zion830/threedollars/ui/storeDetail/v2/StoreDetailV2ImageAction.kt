package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel

internal fun StoreDetailScreenModel.imageIndexFor(action: SDCustomActionModel): Int {
    val cards = sections.filterIsInstance<StoreDetailSectionModel.Image>().firstOrNull()?.cards.orEmpty()
    action.extraParams.valueAsString("IMAGE_INDEX")?.toIntOrNull()?.let { index ->
        if (index in cards.indices) return index
    }
    action.extraParams.valueAsString("IMAGE_ID")?.let { imageId ->
        cards.indexOfFirst { card ->
            card.cardId == imageId || card.cardId.substringAfter(':') == imageId
        }.takeIf { it >= 0 }?.let { return it }
    }
    action.extraParams.valueAsString("IMAGE_URL")?.let { imageUrl ->
        cards.indexOfFirst { it.image.url == imageUrl }.takeIf { it >= 0 }?.let { return it }
    }
    return 0
}

private fun Map<String, SDClickLogValue>.valueAsString(key: String): String? = when (val value = this[key]) {
    is SDClickLogValue.StringValue -> value.value
    is SDClickLogValue.IntValue -> value.value.toString()
    is SDClickLogValue.LongValue -> value.value.toString()
    is SDClickLogValue.DoubleValue -> value.value.toString()
    is SDClickLogValue.BoolValue -> value.value.toString()
    SDClickLogValue.Null, null -> null
}
