package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class Visits(
    @SerializedName("counts")
    val counts: Counts = Counts(),
    @SerializedName("histories")
    val histories: Histories = Histories()
)