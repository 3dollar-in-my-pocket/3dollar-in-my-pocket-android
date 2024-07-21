package com.threedollar.network.data.visit_history


import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import com.threedollar.network.data.store.StoreInfo
import com.threedollar.network.data.store.Visit

@JsonClass(generateAdapter = true)
data class MyVisitHistoryV2(
    @SerializedName("visit")
    val visit: Visit = Visit(),
    @SerializedName("store")
    val store: StoreInfo = StoreInfo(),
)