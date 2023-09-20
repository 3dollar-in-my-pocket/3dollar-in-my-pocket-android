package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName

data class Acquisition(
    @SerializedName("description")
    val description: String = ""
)