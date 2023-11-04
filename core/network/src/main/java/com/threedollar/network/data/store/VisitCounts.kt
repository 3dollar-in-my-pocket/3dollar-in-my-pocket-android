package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class VisitCounts(
    @SerializedName("existsCounts")
    val existsCounts: Int? = 0,
    @SerializedName("isCertified")
    val isCertified: Boolean? = false,
    @SerializedName("notExistsCounts")
    val notExistsCounts: Int? = 0
)