package com.home.domain.data.store


data class OpenStatusModel(
    val openStartDateTime: String? = "",
    val status: StatusType = StatusType.CLOSED
)