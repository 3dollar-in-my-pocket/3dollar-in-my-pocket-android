package com.home.domain.data.store

data class OpeningHoursModel(
    val endTime: String = "",
    val startTime: String = ""
) {
    fun toConvert() = "$startTime - $endTime"
}