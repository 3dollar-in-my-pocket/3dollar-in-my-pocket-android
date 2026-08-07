package com.zion830.threedollars.utils

import com.naver.maps.geometry.LatLng

data class ShareFormat(
    val url: String,
    val storeName: String,
    val location: LatLng?
) {
    val shareUrl: String
        get() = location?.let { "$url${storeName.trim()},${it.latitude},${it.longitude}" } ?: url
}
