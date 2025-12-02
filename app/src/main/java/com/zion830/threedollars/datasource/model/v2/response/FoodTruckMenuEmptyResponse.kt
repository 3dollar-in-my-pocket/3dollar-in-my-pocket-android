package com.zion830.threedollars.datasource.model.v2.response

import com.home.domain.data.store.BossStoreDetailItem
import com.zion830.threedollars.R
import com.threedollar.common.R as CommonR

data class FoodTruckMenuEmptyResponse(
    val emptyImage: Int = R.drawable.ic_food_truck_menu_empty,
    val emptyTitle: Int = CommonR.string.food_truck_menu_empty,
) : BossStoreDetailItem