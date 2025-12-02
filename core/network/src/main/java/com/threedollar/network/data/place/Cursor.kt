package com.threedollar.network.data.place


import com.google.gson.annotations.SerializedName

data class Cursor(
    @SerializedName("hasMore")
    val hasMore: Boolean = false,
    @SerializedName("nextCursor")
    val nextCursor: String? = ""
)