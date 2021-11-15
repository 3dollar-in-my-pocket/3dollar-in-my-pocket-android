package com.zion830.threedollars

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk
import com.zion830.threedollars.repository.model.LoginType
import com.zion830.threedollars.utils.SharedPrefUtils
import io.hackle.android.HackleApp


class GlobalApplication : Application() {

    companion object {
        private lateinit var APPLICATION_CONTEXT: Context
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
        var isLoggedIn: Boolean = false
            private set
        var loginPlatform: LoginType = LoginType.NONE
            private set

        @JvmStatic
        fun getContext(): Context {
            return APPLICATION_CONTEXT
        }
    }

    override fun onCreate() {
        super.onCreate()
        APPLICATION_CONTEXT = applicationContext

        RequestConfiguration.Builder().setTestDeviceIds(listOf(applicationContext.getString(R.string.test_device))).build()
        MobileAds.initialize(this)
        HackleApp.initializeApp(this, BuildConfig.HACKLE_KEY)
        KakaoSdk.init(this, BuildConfig.KAKAO_KEY)
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NMF_CLIENT_ID)

        isLoggedIn = !SharedPrefUtils.getLoginType().isNullOrBlank()
        if (isLoggedIn) {
            loginPlatform = LoginType.of(SharedPrefUtils.getLoginType())
        }
    }
}