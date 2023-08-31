package com.zion830.threedollars.datasource.model.v4.common


import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("accountId")
    val accountId: String = "",
    @SerializedName("accountType")
    val accountType: String = "",
)