package com.threedollar.domain.home.data.store

import java.io.Serializable

data class LocationModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Serializable