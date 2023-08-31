package com.zion830.threedollars.datasource.model.v4.aroundStore


import com.google.gson.annotations.SerializedName

data class VisitCounts(
    @SerializedName("existsCounts")
    val existsCounts: Int = 0,
    @SerializedName("isCertified")
    val isCertified: Boolean = false,
    @SerializedName("notExistsCounts")
    val notExistsCounts: Int = 0,
)