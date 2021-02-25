package com.zion830.threedollars.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import java.util.*


fun showToast(@StringRes resId: Int) {
    Toast.makeText(GlobalApplication.getContext(), GlobalApplication.getContext().getString(resId), Toast.LENGTH_SHORT).show()
}

fun showToast(text: String) {
    Toast.makeText(GlobalApplication.getContext(), text, Toast.LENGTH_SHORT).show()
}

fun Activity.requestPermissionFirst(permission: String = ACCESS_FINE_LOCATION) {
    if (SharedPrefUtils.isFirstPermissionCheck()) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
    }
}

fun Activity.requestPermissionIfNeeds(permission: String = ACCESS_FINE_LOCATION) {
    when {
        isLocationAvailable() -> return
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
        }
        else -> {
            if (SharedPrefUtils.isFirstPermissionCheck()) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
            } else {
                openPermissionSettingPage()
            }
        }
    }
}

fun isLocationAvailable(): Boolean =
    ContextCompat.checkSelfPermission(GlobalApplication.getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun isGpsAvailable(): Boolean {
    val locationManager = GlobalApplication.getContext().getSystemService(LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun getCurrentLocationName(location: LatLng): String? {
    val geoCoder = Geocoder(GlobalApplication.getContext(), Locale.KOREA)
    return try {
        val addresses: List<Address> = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses.isEmpty()) {
            GlobalApplication.getContext().getString(R.string.location_no_address)
        } else {
            with(addresses[0]) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }.joinToString().substringAfter(" ")
        }
    } catch (e: Exception) {
        Log.e("getCurrentLocationName", e.message ?: "")
        GlobalApplication.getContext().getString(R.string.location_no_address)
    }
}

private fun Activity.openPermissionSettingPage() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}