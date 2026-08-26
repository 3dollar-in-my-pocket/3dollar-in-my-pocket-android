package com.zion830.threedollars.ui.splash.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.loadImage
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.databinding.ActivitySplashBinding
import com.zion830.threedollars.ui.login.ui.LoginActivity
import com.zion830.threedollars.ui.splash.viewModel.SplashViewModel
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2Activity
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.isLocationServiceEnabled
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.showToast
import com.zion830.threedollars.ui.dialog.VersionUpdateDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class SplashActivity :
    BaseActivity<ActivitySplashBinding, SplashViewModel>({ ActivitySplashBinding.inflate(it) }) {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: SplashViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val appUpdateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                showToast("앱 업데이트에 실패했습니다.")
                viewModel.checkForceUpdate()
            } else {
                viewModel.checkAccessToken()
            }
        }

    override fun initView() {
        setDarkSystemBars()
        initAdvertisements()
        initPushToken()
        initFlow()
        initAppUpdate()
    }

    private fun initAppUpdate() {
        lifecycleScope.launch {
            delay(2000L)
            if (BuildConfig.DEBUG) {
                viewModel.checkForceUpdate()
            } else {
                inAppUpdateIfNeeded()
                viewModel.checkForceUpdate()
            }
        }
    }

    private suspend fun inAppUpdateIfNeeded() {
        suspendCancellableCoroutine { continuation ->
            val appUpdateManager = AppUpdateManagerFactory.create(this@SplashActivity)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        appUpdateLauncher,
                        updateOptions
                    )
                }
                continuation.resume(Unit)
            }

            appUpdateInfoTask.addOnFailureListener {
                continuation.resume(Unit)
            }
        }
    }

    // 푸시 토큰 등록은 인증이 필요한 API라 비로그인 상태에서 호출하면 401이 내려온다. 토큰은 로그인 성공 시점에 등록된다.
    private fun initPushToken() {
        if (LegacySharedPrefUtils.getAccessToken().isNullOrBlank()) return

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.putPushInformation(PushInformationRequest(pushToken = it.result))
            }
        }
    }

    private fun initAdvertisements() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (isLocationAvailable() && isLocationServiceEnabled()) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener {
                if (it != null) {
                    // 홈 진입 시 위치 획득에 실패해도 재사용할 수 있도록 저장해둔다.
                    sharedPrefUtils.saveUserLastLocation(latitude = it.latitude, longitude = it.longitude)
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
                                        if (accessCheckModel.resultCode != "UA000") {
                                            accessCheckModel.message?.let {
                                                showToast(it)
                                            }
                                        }
                                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.shouldShowUpdateDialog.collect { updateDialog ->
                        updateDialog?.let {
                            VersionUpdateDialog.getInstance(it)
                                .show(supportFragmentManager, VersionUpdateDialog::class.java.name)
                        }
                    }
                }
            }
        }
    }

    private fun tryLogin() {
        val deepLink = intent.getStringExtra(STORE_TYPE) ?: intent.getStringExtra(PUSH_LINK) ?: ""
        when {
            deepLink == getString(CommonR.string.scheme_host_kakao_link_food_truck_type) -> {
                startActivity(
                    StoreDetailV2Activity.getIntent(
                        context = this@SplashActivity,
                        storeType = com.threedollar.common.utils.Constants.BOSS_STORE,
                        deepLinkStoreId = intent.getStringExtra(STORE_ID),
                    ),
                )
            }

            deepLink == getString(CommonR.string.scheme_host_kakao_link_road_food_type) -> {
                startActivity(
                    StoreDetailV2Activity.getIntent(
                        context = this@SplashActivity,
                        storeType = com.threedollar.common.utils.Constants.USER_STORE,
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
            .setTitle(msg ?: getString(CommonR.string.server_500))
            .setMessage(msg ?: getString(CommonR.string.server_500_msg))
            .setCancelable(false)
            .create()
            .show()
    }

    companion object {
        private const val STORE_ID = "storeId"
        private const val STORE_TYPE = "STORE_TYPE"
        private const val PUSH_LINK = "link"

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
