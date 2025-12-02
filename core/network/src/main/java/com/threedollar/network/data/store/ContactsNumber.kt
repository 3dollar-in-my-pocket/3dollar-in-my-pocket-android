package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class ContactsNumber(
    @SerializedName("number")
    val number: String? = "",
    @SerializedName("description")
    val description: String? = "",
)