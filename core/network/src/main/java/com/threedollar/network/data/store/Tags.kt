package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Tags(
    @SerializedName("isNew")
    val isNew: Boolean? = false,
    @SerializedName("hasIssuableCoupon")
    val hasIssuableCoupon: Boolean? = false,
    @SerializedName("isVerifiedStore")
    val isVerifiedStore: Boolean? = false
)