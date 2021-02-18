package com.zion830.threedollars.utils

import android.location.Location
import androidx.annotation.DrawableRes
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

fun GoogleMap.setDefaultMarker(latLng: LatLng?) {
    if (latLng == null) {
        return
    }

    val size = SizeUtils.dpToPx(19f)
    val bitmap = getBitmapDescriptor(R.drawable.ic_store_selected, size, size)
    addMarker(MarkerOptions().position(latLng).icon(bitmap))
}

fun GoogleMap.setMarker(latLng: LatLng?, @DrawableRes drawableId: Int, width: Float, height: Float) {
    if (latLng == null) {
        return
    }

    val bitmap = getBitmapDescriptor(drawableId, SizeUtils.dpToPx(width), SizeUtils.dpToPx(height))
    addMarker(MarkerOptions().position(latLng).icon(bitmap))
}

fun GoogleMap.setDefaultMarkers(storeList: List<LatLng>) {
    clear()
    val size = SizeUtils.dpToPx(19f)
    val bitmap = getBitmapDescriptor(R.drawable.ic_store_selected, size, size)
    storeList.forEach {
        addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).icon(bitmap))
    }
}