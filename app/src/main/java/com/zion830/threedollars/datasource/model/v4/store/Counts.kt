package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class Counts(
    @SerializedName("existsCounts")
    val existsCounts: Int = 0,
    @SerializedName("isCertified")
    val isCertified: Boolean = false,
    @SerializedName("notExistsCounts")
    val notExistsCounts: Int = 0,
)