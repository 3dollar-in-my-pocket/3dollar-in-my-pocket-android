package com.zion830.threedollars

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.firebase.analytics.FirebaseAnalytics
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.utils.GlobalEvent
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.ui.login.ui.LoginActivity
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class GlobalApplication : Application() {

    companion object {
        const val DYNAMIC_LINK = "https://link.threedollars.co.kr"
        lateinit var instance: GlobalApplication
            private set

        private lateinit var APPLICATION_CONTEXT: Context
        lateinit var eventTracker: FirebaseAnalytics
            private set

        private val googleSignInOptions by lazy {
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
                .requestProfile()
                .requestIdToken(getContext().getString(R.string.default_web_client_id))
                .requestServerAuthCode(getContext().getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        }
        val googleClient: GoogleSignInClient by lazy {
            GoogleSignIn.getClient(APPLICATION_CONTEXT, googleSignInOptions)
        }
        val isLoggedIn: Boolean
            get() = !LegacySharedPrefUtils.getLoginType().isNullOrBlank()
        var loginPlatform: LoginType = LoginType.NONE
            private set

        var storeMarker: AdvertisementModelV2? = null

        @JvmStatic
        fun getContext(): Context {
            return APPLICATION_CONTEXT
        }
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        instance = this
        APPLICATION_CONTEXT = applicationContext
        eventTracker = FirebaseAnalytics.getInstance(APPLICATION_CONTEXT)
        LogManager.initialize(eventTracker)
        com.threedollar.common.analytics.SDClickLogger.initialize(eventTracker)

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(DEVICE_ID_EMULATOR))
                .build()
        )
        MobileAds.initialize(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_KEY)
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NMF_CLIENT_ID)

        if (isLoggedIn) {
            loginPlatform = LoginType.of(LegacySharedPrefUtils.getLoginType())
        }

        observeSessionExpired()
    }

    /**
     * 세션 만료(401)를 프로세스 단위로 관찰한다.
     *
     * 딥링크/푸시로 진입한 화면은 [MainActivity]가 아닐 수 있어 화면별로 관찰하면 누락된다.
     * 이벤트는 로그인에 성공해 [MainActivity]에 진입할 때 초기화되므로,
     * 세션이 만료된 동안 요청이 여러 번 실패해도 로그인 화면으로 한 번만 이동한다.
     */
    private fun observeSessionExpired() {
        applicationScope.launch {
            GlobalEvent.logoutEvent.collect { isSessionExpired ->
                if (isSessionExpired) {
                    moveToLoginBySessionExpired()
                }
            }
        }
    }

    private fun moveToLoginBySessionExpired() {
        LegacySharedPrefUtils.clearUserInfo()
        loginPlatform = LoginType.NONE
        startActivity(LoginActivity.getSessionExpiredIntent(this))
    }
}
