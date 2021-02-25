package com.zion830.threedollars

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk

class GlobalApplication : Application() {

    companion object {
        private lateinit var APPLICATION_CONTEXT: Context

        @JvmStatic
        fun getContext(): Context {
            return APPLICATION_CONTEXT
        }
    }

    override fun onCreate() {
        super.onCreate()
        APPLICATION_CONTEXT = applicationContext

        KakaoSdk.init(this, BuildConfig.KAKAO_KEY)
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NMF_CLIENT_ID)
        MobileAds.initialize(this)
    }
}