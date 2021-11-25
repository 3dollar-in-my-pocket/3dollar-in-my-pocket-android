package com.zion830.threedollars.repository.model.v2.response.store

import com.google.gson.annotations.SerializedName

data class VisitHistory(
    @SerializedName("existsCounts")
    val existsCounts: Int = 0,
    @SerializedName("isCertified")
    val isCertified: Boolean = false,
    @SerializedName("notExistsCounts")
    val notExistsCounts: Int = 0
)