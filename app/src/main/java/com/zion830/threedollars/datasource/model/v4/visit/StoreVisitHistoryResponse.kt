package com.zion830.threedollars.datasource.model.v4.visit


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Cursor

data class StoreVisitHistoryResponse(
    @SerializedName("contents")
    val storeVisitModels: List<StoreVisitModel> = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor = Cursor()
)