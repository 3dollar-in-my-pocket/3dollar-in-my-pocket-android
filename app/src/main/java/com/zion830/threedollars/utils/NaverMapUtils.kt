package com.zion830.threedollars.utils

import android.location.Location
import com.naver.maps.geometry.LatLng


object NaverMapUtils {
    val DEFAULT_LOCATION = LatLng(37.56, 126.97) // 서울
    val DEFAULT_DISTANCE_M = 100000.0
    const val LOCATION_PERMISSION_REQUEST_CODE = 1000

    fun calculateDistance(first: LatLng?, second: LatLng?): Float {
        if (first == null || second == null) {
            return 1000f
        }

        val result = FloatArray(3)
        Location.distanceBetween(
            first.latitude,
            first.longitude,
            second.latitude,
            second.longitude,
            result
        )

        return result[0]
    }
}
