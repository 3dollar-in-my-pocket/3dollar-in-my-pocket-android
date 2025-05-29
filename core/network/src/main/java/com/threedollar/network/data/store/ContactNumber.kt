package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class ContactNumber(
    @SerializedName("number")
    val number: String = "",

    @SerializedName("description")
    val description: String? = ""
)