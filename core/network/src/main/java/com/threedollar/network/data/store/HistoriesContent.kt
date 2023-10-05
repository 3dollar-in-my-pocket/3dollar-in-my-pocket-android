package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class HistoriesContent(
    @SerializedName("visit")
    val visit: Visit = Visit(),
    @SerializedName("visitor")
    val visitor: Visitor = Visitor()
)