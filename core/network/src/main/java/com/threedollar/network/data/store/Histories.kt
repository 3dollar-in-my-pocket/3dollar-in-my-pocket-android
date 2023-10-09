package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class Histories(
    @SerializedName("contents")
    val contents: List<HistoriesContent>? = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor? = Cursor()
)