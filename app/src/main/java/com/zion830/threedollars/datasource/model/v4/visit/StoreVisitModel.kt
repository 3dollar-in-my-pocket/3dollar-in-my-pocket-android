package com.zion830.threedollars.datasource.model.v4.visit


import com.google.gson.annotations.SerializedName

data class StoreVisitModel(
    @SerializedName("visit")
    val visit: Visit = Visit(),
    @SerializedName("visitor")
    val visitor: Visitor = Visitor()
)