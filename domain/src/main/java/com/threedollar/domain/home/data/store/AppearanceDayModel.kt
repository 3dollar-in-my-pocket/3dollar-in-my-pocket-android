package com.threedollar.domain.home.data.store

data class AppearanceDayModel(
    val dayOfTheWeek: DayOfTheWeekType,
    val locationDescription: String = "-",
    val openingHoursModel: OpeningHoursModel? = null
)