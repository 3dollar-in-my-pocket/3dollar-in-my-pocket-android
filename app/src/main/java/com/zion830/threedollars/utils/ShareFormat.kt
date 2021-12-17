package com.zion830.threedollars.utils

import com.naver.maps.geometry.LatLng

data class ShareFormat(
    val url: String,
    val storeName: String,
    val location: LatLng?
) {
    val shareUrl = "$url${storeName.trim()},${location?.latitude},${location?.longitude}"
}