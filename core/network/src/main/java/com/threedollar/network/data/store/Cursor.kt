package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Cursor(
    @SerializedName("hasMore")
    val hasMore: Boolean? = false,
    @SerializedName("nextCursor")
    val nextCursor: String? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)