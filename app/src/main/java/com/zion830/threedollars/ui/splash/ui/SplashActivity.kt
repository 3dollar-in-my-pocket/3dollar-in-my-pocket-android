package com.zion830.threedollars.ui.splash.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.loadImage
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivitySplashBinding
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.ui.login.ui.LoginActivity
import com.zion830.threedollars.ui.splash.viewModel.SplashViewModel
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.requestPermissionFirst
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity :
    BaseActivity<ActivitySplashBinding, SplashViewModel>({ ActivitySplashBinding.inflate(it) }) {

    override val viewModel: SplashViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun initView() {
        initAdvertisements()
        initPushToken()
        initFlow()
        initAppUpdate()
    }

    private fun initAppUpdate() {
        lifecycleScope.launch {
            delay(2000L)
            if (BuildConfig.DEBUG) {
                viewModel.checkAccessToken()
            } else {
                val appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
                val appUpdateInfoTask = appUpdateManager.appUpdateInfo
                appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    ) {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this@SplashActivity,
                            APP_UPDATE_CODE
                        )
                    } else {
                        viewModel.checkAccessToken()
                    }
                }
                appUpdateInfoTask.addOnFailureListener {
                    viewModel.checkAccessToken()
                }
            }
        }
    }

    private fun initPushToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.putPushInformation(PushInformationRequest(pushToken = it.result))
            }
        }
    }

    private fun initAdvertisements() {
        requestPermissionFirst()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (isLocationAvailable() && isGpsAvailable()) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener {
                if (it != null) {
                    viewModel.getStoreMarkerAdvertisements(
                        latLng = LatLng(it.latitude, it.longitude)
                    )
                    viewModel.getSplashAdvertisements(
                        latLng = LatLng(it.latitude, it.longitude)
                    )
                }
            }
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "SplashActivity", screenName = null)
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
                launch {
                    viewModel.splashAdvertisement.collect { advertisement ->
                        advertisement?.let {
                            binding.advertisementImageView.loadImage(advertisement.image.url)
                        }
                    }
                }
                launch {
                    viewModel.accessCheckModel.collect { accessCheckModel ->
                        accessCheckModel?.let {
                            if (accessCheckModel.ok) {
                                tryLogin()
                            } else {
                                when (accessCheckModel.resultCode) {
                                    "503" -> showAlertDialog()
                                    else -> {
                                        accessCheckModel.message?.let {
                                            showToast(it)
                                        }
                                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                showToast("앱 업데이트에 실패했습니다. 다시시도해주세요.")
            }
            viewModel.checkAccessToken()
        }
    }

    private fun tryLogin() {
        val deepLink = intent.getStringExtra(STORE_TYPE) ?: intent.getStringExtra(PUSH_LINK) ?: ""
        when {
            deepLink == getString(R.string.scheme_host_kakao_link_food_truck_type) -> {
                startActivity(
                    BossStoreDetailActivity.getIntent(
                        this@SplashActivity,
                        deepLinkStoreId = intent.getStringExtra(STORE_ID),
                    ),
                )
            }

            deepLink == getString(R.string.scheme_host_kakao_link_road_food_type) -> {
                startActivity(
                    StoreDetailActivity.getIntent(
                        this@SplashActivity,
                        deepLinkStoreId = intent.getStringExtra(STORE_ID),
                    ),
                )
            }

            deepLink.contains("dollars") -> {
                startActivity(
                    Intent(this@SplashActivity, DynamicLinkActivity::class.java).apply {
                        putExtra("link", deepLink)
                    },
                )
            }

            else -> {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
        }
        finish()
    }

    private fun showAlertDialog(msg: String? = null) {
        AlertDialog.Builder(this@SplashActivity)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
            .setTitle(msg ?: getString(R.string.server_500))
            .setMessage(msg ?: getString(R.string.server_500_msg))
            .setCancelable(false)
            .create()
            .show()
    }

    companion object {
        private const val STORE_ID = "storeId"
        private const val STORE_TYPE = "STORE_TYPE"
        private const val PUSH_LINK = "link"
        private const val GOOGLE_LOGIN_ERROR_REQUEST_CODE = 1001
        private const val APP_UPDATE_CODE = 1002

        fun getIntent(
            context: Context,
            deepLink: Uri? = null,
            storeType: String? = null,
        ) =
            Intent(context, SplashActivity::class.java).apply {
                deepLink?.let {
                    val deepLinkStoreId = it.getQueryParameter(STORE_ID) ?: run {
                        // 딥링크 name 파라미터 없음 오류 로그남기기
                        // DeepLink does'nt have name query parameter
                        ""
                    }
                    putExtra(STORE_ID, deepLinkStoreId)
                }
                storeType?.let {
                    putExtra(STORE_TYPE, it)
                }
            }
    }
}
