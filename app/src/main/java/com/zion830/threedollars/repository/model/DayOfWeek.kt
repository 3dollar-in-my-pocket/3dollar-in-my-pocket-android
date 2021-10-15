package com.zion830.threedollars.repository.model

import com.google.gson.annotations.SerializedName

enum class DayOfWeek {
    @SerializedName("MONDAY")
    MONDAY,

    @SerializedName("TUESDAY")
    TUESDAY,

    @SerializedName("WEDNESDAY")
    WEDNESDAY,

    @SerializedName("THURSDAY")
    THURSDAY,

    @SerializedName("FRIDAY")
    FRIDAY,

    @SerializedName("SATURDAY")
    SATURDAY,

    @SerializedName("SUNDAY")
    SUNDAY,
}