package com.zion830.threedollars.datasource.model.v2.response

import com.threedollar.common.data.AdAndStoreItem
import com.zion830.threedollars.R
import com.threedollar.common.R as CommonR

data class StoreEmptyResponse(
    val emptyImage: Int = R.drawable.ic_no_store,
    val emptyTitle: Int = CommonR.string.empty_store,
    val emptyBody: Int = CommonR.string.empty_store_msg
) : AdAndStoreItem