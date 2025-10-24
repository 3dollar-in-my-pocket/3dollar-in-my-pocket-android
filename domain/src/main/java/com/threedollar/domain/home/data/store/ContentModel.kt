package com.threedollar.domain.home.data.store

import com.threedollar.common.data.AdAndStoreItem

data class ContentModel(
    val storeModel: StoreModel = StoreModel(),
    val markerModel: MarkerModel? = null,
    val openStatusModel: OpenStatusModel = OpenStatusModel(),
    val distanceM: Int = 0,
    val extraModel: ExtraModel = ExtraModel()
) : AdAndStoreItem