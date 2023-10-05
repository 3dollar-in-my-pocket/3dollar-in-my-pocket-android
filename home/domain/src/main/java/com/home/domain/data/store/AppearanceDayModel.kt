package com.home.domain.data.store

data class AppearanceDayModel(
    val dayOfTheWeek: DayOfTheWeekType,
    val locationDescription: String = "-",
    val openingHoursModel: OpeningHoursModel? = null
)