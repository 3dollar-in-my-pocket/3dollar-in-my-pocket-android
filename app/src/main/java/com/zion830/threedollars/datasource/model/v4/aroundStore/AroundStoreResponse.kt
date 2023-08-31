package com.zion830.threedollars.datasource.model.v4.aroundStore


import com.google.gson.annotations.SerializedName

data class AroundStoreResponse(
    @SerializedName("contents")
    val contents: List<Content> = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor = Cursor(),
)