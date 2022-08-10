package com.zion830.threedollars.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import java.util.*


fun showToast(@StringRes resId: Int) {
    Toast.makeText(
        GlobalApplication.getContext(),
        GlobalApplication.getContext().getString(resId),
        Toast.LENGTH_SHORT
    ).show()
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
    ContextCompat.checkSelfPermission(
        GlobalApplication.getContext(),
        ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

fun isGpsAvailable(): Boolean {
    val locationManager =
        GlobalApplication.getContext().getSystemService(LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun getCurrentLocationName(location: LatLng?): String? {
    val notFindMsg = GlobalApplication.getContext().getString(R.string.location_no_address)
    if (location == null) {
        return notFindMsg
    }

    val geoCoder = Geocoder(GlobalApplication.getContext(), Locale.KOREA)
    return try {
        val addresses: List<Address> =
            geoCoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
        if (addresses.isEmpty()) {
            notFindMsg
        } else {
            with(addresses[0]) {
                (0..maxAddressLineIndex).map {
                    getAddressLine(it)
                }
            }.joinToString().substringAfter(" ")
        }
    } catch (e: Exception) {
        Log.e("getCurrentLocationName", e.message ?: "")
        notFindMsg
    }
}

private fun Activity.openPermissionSettingPage() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    intent.data = Uri.fromParts("package", packageName, null)
    startActivity(intent)
}

fun Context.getInstalledInfo() =
    """
        -------------------------------------------------------
        ${getString(R.string.app_name)}
        ${getString(R.string.android_version)}: ${Build.VERSION.SDK_INT}
        ------------------------------------------------------- 
    """.trimIndent()

fun Context.shareUrl(url: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }
    startActivity(Intent.createChooser(sendIntent, url))
}

fun Context.shareWithKakao(shareFormat: ShareFormat) {
    if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
        val feed = FeedTemplate(
            content = Content(
                title = getString(R.string.share_kakao_title),
                description = getString(R.string.share_kakao),
                imageUrl = BuildConfig.BASE_URL + "api/images/share-with-kakao.png",
                link = Link(getString(R.string.download_url), getString(R.string.download_url))
            ),
            buttons = listOf(
                Button(
                    title = getString(R.string.find_location),
                    link = Link(shareFormat.shareUrl, shareFormat.shareUrl)
                )
            )
        )

        LinkClient.instance.defaultTemplate(this, feed) { linkResult, error ->
            if (error != null) {
                shareUrl(shareFormat.url)
            } else if (linkResult != null) {
                this.startActivity(linkResult.intent)
            }
        }
    } else {
        shareUrl(shareFormat.shareUrl)
    }
}

fun Context.goToPermissionSetting() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", applicationContext.packageName, null)
        }
    )
}