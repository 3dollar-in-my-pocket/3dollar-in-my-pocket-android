package com.threedollar.domain.home.request

import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType

data class UserStoreModelRequest(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val storeName: String? = null,
    val salesType: String? = null,
    val appearanceDays: List<DayOfTheWeekType>? = null,
    val openingHours: OpeningHourRequest? = null,
    val paymentMethods: List<PaymentType>? = null,
    val menuRequests: List<MenuModelRequest>? = null,
)
