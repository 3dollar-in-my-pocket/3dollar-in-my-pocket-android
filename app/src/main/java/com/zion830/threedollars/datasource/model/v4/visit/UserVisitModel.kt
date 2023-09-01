package com.zion830.threedollars.datasource.model.v4.visit


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.storeReview.Store

data class UserVisitModel(
    @SerializedName("store")
    val store: Store = Store(),
    @SerializedName("visit")
    val visit: Visit = Visit()
)