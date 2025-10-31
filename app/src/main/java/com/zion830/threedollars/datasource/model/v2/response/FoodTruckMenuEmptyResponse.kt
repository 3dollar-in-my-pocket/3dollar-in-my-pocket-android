package com.zion830.threedollars.datasource.model.v2.response

import com.threedollar.domain.home.data.store.BossStoreDetailItem
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.threedollar.common.R as CommonR

data class FoodTruckMenuEmptyResponse(
    val emptyImage: Int = DesignSystemR.drawable.ic_food_truck_menu_empty,
    val emptyTitle: Int = CommonR.string.food_truck_menu_empty,
) : BossStoreDetailItem