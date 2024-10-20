package com.zion830.threedollars.utils

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.user.UserApiClient
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.CustomToastBlackBinding
import com.zion830.threedollars.ui.login.ui.LoginActivity
import org.json.JSONObject
import java.util.Locale


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

fun showCustomBlackToast(text: String) {
    val inflater = LayoutInflater.from(GlobalApplication.getContext())
    val binding: CustomToastBlackBinding =
        DataBindingUtil.inflate(inflater, R.layout.custom_toast_black, null, false)

    binding.toastTextView.text = text
    Toast(GlobalApplication.getContext()).apply {
        setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        setMargin(0f, 0.1f)
        duration = Toast.LENGTH_SHORT
        view = binding.root
    }.show()
}

fun Activity.requestPermissionFirst(permission: String = ACCESS_FINE_LOCATION) {
    if (LegacySharedPrefUtils.isFirstPermissionCheck()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(permission, POST_NOTIFICATIONS), 0)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
        }
    }
}

fun Activity.requestPermissionIfNeeds(permission: String = ACCESS_FINE_LOCATION) {
    when {
        isLocationAvailable() -> return
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
        }

        else -> {
            if (LegacySharedPrefUtils.isFirstPermissionCheck()) {
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
        ${getString(R.string.app_name_3dollar)}
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

fun Context.shareWithKakao(
    shareFormat: ShareFormat,
    title: String?,
    description: String?,
    imageUrl: String?,
    storeId: String?,
    type: String?
) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
        val feed = FeedTemplate(
            content = Content(
                title = title ?: "",
                description = description,
                imageUrl = imageUrl ?: "",
                link = Link(getString(R.string.download_url), getString(R.string.download_url))
            ),
            buttons = listOf(
                Button(
                    title = getString(R.string.find_location),
                    link = Link(
                        webUrl = shareFormat.shareUrl,
                        mobileWebUrl = shareFormat.shareUrl,
                        androidExecutionParams = mapOf("storeId" to storeId.toString(), "storeType" to type.toString()),
                        iosExecutionParams = mapOf("storeId" to storeId.toString(), "storeType" to type.toString())
                    )
                )
            )
        )

        ShareClient.instance.shareDefault(this, feed) { linkResult, error ->
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

fun String.getErrorMessage(): String {
    return try {
        JSONObject(this).getString("message")
    } catch (e: Exception) {
        ""
    }
}

fun String.urlToBitmap(): FutureTarget<Bitmap> =
    Glide.with(GlobalApplication.getContext()).asBitmap().load(this).diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean) = false

            override fun onResourceReady(
                resource: Bitmap,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }).submit()

fun Activity.navigateToMainActivityOnCloseIfNeeded() {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val isBackMainActivity = manager.appTasks.isEmpty() || manager.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            it.appTasks.forEach { task ->
                if (task.taskInfo.baseActivity?.className?.contains("MainActivity") == true) return@let false
            }
            return@let true
        } else {
            it.getRunningTasks(10).forEach { task ->
                if (task.topActivity?.className?.contains("MainActivity") == true) return@let false
            }
            return@let true
        }
    }
    if (isBackMainActivity && GlobalApplication.isLoggedIn) startActivity(MainActivity.getIntent(this))
    else if (!GlobalApplication.isLoggedIn) startActivity(Intent(this, LoginActivity::class.java))
}

fun NavController.navigateSafe(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null,
) {
    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
    // 현재 fragment의 id와 이동할 fragment의 id가 다르면 화면이동 실행 (같다는 건, 이미 이동이 된 후이기 때문)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(resId, args, navOptions, navExtras)
    }
}

fun subscribeToTopicFirebase(isSubscribe: Boolean) {
    if (isSubscribe) {
        Firebase.messaging.subscribeToTopic("marketing_aos").addOnCompleteListener {
            if (it.isSuccessful) {
                if (!LegacySharedPrefUtils.getFirstMarketing()) LegacySharedPrefUtils.setFirstMarketing()
            }
        }
    } else {
        Firebase.messaging.unsubscribeFromTopic("marketing_aos")
    }
}