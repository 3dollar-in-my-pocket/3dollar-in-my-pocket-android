package com.threedollar.domain.home.request

import com.threedollar.domain.home.data.store.DayOfTheWeekType
import com.threedollar.domain.home.data.store.PaymentType

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
