package com.home.domain.request

import com.home.domain.data.store.DayOfTheWeekType
import com.home.domain.data.store.PaymentType

data class UserStoreModelRequest(
    val appearanceDays: List<DayOfTheWeekType>,
    val latitude: Double,
    val longitude: Double,
    val menuRequests: List<MenuModelRequest>,
    val paymentMethods: List<PaymentType>,
    val openingHours: OpeningHourRequest? = null,
    val storeName: String,
    val storeType: String? = null,
)
