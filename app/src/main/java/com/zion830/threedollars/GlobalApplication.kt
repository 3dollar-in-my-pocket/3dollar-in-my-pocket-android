package com.zion830.threedollars

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
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
        KakaoSdk.init(this, applicationContext.getString(R.string.kakao_key))
    }
}