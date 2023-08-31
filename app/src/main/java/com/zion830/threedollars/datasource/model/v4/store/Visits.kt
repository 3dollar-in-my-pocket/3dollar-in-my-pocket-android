package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class Visits(
    @SerializedName("counts")
    val counts: Counts = Counts(),
    @SerializedName("histories")
    val histories: Histories = Histories()
)