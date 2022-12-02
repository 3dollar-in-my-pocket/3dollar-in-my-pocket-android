package com.zion830.threedollars.datasource.model.v2.response.store

data class AppearanceDayModel(
    var dayOfTheWeek: String?,
    val locationDescription: String? = "-",
    val openingHours: String? = "휴무"
)