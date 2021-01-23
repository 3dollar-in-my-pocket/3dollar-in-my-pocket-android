package com.zion830.threedollars.utils

import android.location.Location
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.zion830.threedollars.R


const val DEFAULT_ZOOM = 15f
val DEFAULT_LOCATION = LatLng(37.56, 126.97) // 서울

fun Location?.toLatLng() = if (this == null) {
    DEFAULT_LOCATION
} else {
    LatLng(latitude, longitude)
}

fun GoogleMap.setMarker(latLng: LatLng?) {
    if (latLng == null) {
        return
    }
    clear()
    val bitmap = getBitmapDescriptor(R.drawable.ic_store_selected)
    addMarker(MarkerOptions().position(latLng).icon(bitmap))
}

fun GoogleMap.setMarkers(storeList: List<LatLng>) {
    clear()
    val bitmap = getBitmapDescriptor(R.drawable.ic_store_selected)
    storeList.forEach {
        addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).icon(bitmap))
    }
}