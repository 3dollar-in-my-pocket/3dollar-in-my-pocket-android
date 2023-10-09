package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class Reviews(
    @SerializedName("contents")
    val contents: List<ReviewContent>? = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor? = Cursor()
)