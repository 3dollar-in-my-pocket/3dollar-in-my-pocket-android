package com.zion830.threedollars.datasource.model.v4.common


import com.google.gson.annotations.SerializedName

data class Cursor(
    @SerializedName("hasMore")
    val hasMore: Boolean = false,
    @SerializedName("nextCursor")
    val nextCursor: String = "",
    @SerializedName("totalCount")
    val totalCount: Int = 0
)