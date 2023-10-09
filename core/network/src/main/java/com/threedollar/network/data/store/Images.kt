package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class Images(
    @SerializedName("contents")
    val contents: List<ImageContent>? = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor? = Cursor()
)