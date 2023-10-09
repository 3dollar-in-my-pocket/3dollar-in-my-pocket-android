package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class AroundStoreResponse(
    @SerializedName("contents")
    val contents: List<Content>? = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor?
)