package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("accountId")
    val accountId: String = "",
    @SerializedName("accountType")
    val accountType: String = ""
)