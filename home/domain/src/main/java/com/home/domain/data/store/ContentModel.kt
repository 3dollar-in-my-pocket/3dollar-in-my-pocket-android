package com.home.domain.data.store

import com.threedollar.common.data.AdAndStoreItem

data class ContentModel(
    val distanceM: Int = 0,
    val extraModel: ExtraModel = ExtraModel(),
    val storeModel: StoreModel = StoreModel()
) : AdAndStoreItem