package com.home.domain.data.store

import java.io.Serializable

data class OpeningHoursModel(
    val endTime: String = "",
    val startTime: String = "",
) : Serializable {
    fun toConvert() = "$startTime - $endTime"
}
