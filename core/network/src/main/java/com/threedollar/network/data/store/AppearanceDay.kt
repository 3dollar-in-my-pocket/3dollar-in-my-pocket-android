package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class AppearanceDay(
    @SerializedName("dayOfTheWeek")
    val dayOfTheWeek: String ="",
    @SerializedName("locationDescription")
    val locationDescription: String ="",
    @SerializedName("openingHours")
    val openingHours: OpeningHours = OpeningHours()
)