package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class AccountNumber(
    @SerializedName("bank")
    val bank: Bank? = Bank(),
    @SerializedName("accountHolder")
    val accountHolder: String? = "",
    @SerializedName("accountNumber")
    val accountNumber: String? = "",
    @SerializedName("description")
    val description: String? = "",
)
