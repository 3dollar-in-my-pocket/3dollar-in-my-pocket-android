package com.zion830.threedollars.repository.model.v2.response

import com.zion830.threedollars.R
import com.zion830.threedollars.repository.model.v2.response.store.BossStoreDetailItem

data class FoodTruckMenuEmptyResponse(
    val emptyImage: Int = R.drawable.ic_food_truck_menu_empty,
    val emptyTitle: Int = R.string.food_truck_menu_empty,
) : BossStoreDetailItem