package com.zion830.threedollars.datasource.model.v4.aroundStore


import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("accountId")
    val accountId: String = "",
    @SerializedName("accountType")
    val accountType: String = "",
)