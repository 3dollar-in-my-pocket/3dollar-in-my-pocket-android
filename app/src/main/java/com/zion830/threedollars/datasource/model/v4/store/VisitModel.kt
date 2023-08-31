package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class VisitModel(
    @SerializedName("visit")
    val visit: Visit = Visit(),
    @SerializedName("visitor")
    val visitor: Visitor = Visitor()
)