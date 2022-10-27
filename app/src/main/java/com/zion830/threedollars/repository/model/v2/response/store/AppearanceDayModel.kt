package com.zion830.threedollars.repository.model.v2.response.store

import com.google.gson.annotations.SerializedName

data class AppearanceDayModel(
    var dayOfTheWeek: String?,
    val locationDescription: String? = "-",
    val openingHours: String? = "휴무"
)