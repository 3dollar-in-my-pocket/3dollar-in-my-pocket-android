package com.zion830.threedollars.utils

import android.location.Location
import com.google.android.libraries.maps.model.LatLng

const val DEFAULT_ZOOM = 15f
val DEFAULT_LOCATION = LatLng(37.56, 126.97) // 서울

fun Location?.toLatLng() = if (this == null) {
    DEFAULT_LOCATION
} else {
    LatLng(latitude, longitude)
}