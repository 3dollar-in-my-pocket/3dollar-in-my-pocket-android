package com.threedollar.domain.home.request

import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType

data class UserStoreModelRequest(
    val latitude: Double,
    val longitude: Double,
    val storeName: String,
    val salesType: String? = null,
    val appearanceDays: List<DayOfTheWeekType>,
    val openingHours: OpeningHourRequest? = null,
    val paymentMethods: List<PaymentType>,
    val menuRequests: List<MenuModelRequest>,
)
