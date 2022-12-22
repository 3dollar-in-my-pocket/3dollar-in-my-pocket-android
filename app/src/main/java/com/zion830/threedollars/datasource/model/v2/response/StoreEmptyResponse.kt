package com.zion830.threedollars.datasource.model.v2.response

import com.zion830.threedollars.R

data class StoreEmptyResponse(
    val emptyImage: Int = R.drawable.ic_no_store,
    val emptyTitle: Int = R.string.empty_store,
    val emptyBody: Int = R.string.empty_store_msg
) : AdAndStoreItem