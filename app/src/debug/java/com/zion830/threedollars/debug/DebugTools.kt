package com.zion830.threedollars.debug

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.threedollar.common.analytics.GALogMonitor

/**
 * 개발 빌드 디버그 도구 진입점. DEV 플로팅 버튼·디버깅 메뉴·GA 로그 기록을 모든 앱 화면에 붙인다.
 * release 빌드에는 같은 이름의 no-op 이 들어간다.
 */
object DebugTools {
    lateinit var preferences: DebugPreferences
        private set
    lateinit var gaLogStore: GALogStore
        private set

    fun install(application: Application) {
        preferences = DebugPreferences(application)
        gaLogStore = GALogStore(isRecordingEnabled = { preferences.isGALogRecordingEnabled })
        GALogMonitor.listener = gaLogStore::record
        application.registerActivityLifecycleCallbacks(OverlayAttacher)
    }

    private object OverlayAttacher : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            if (!activity.javaClass.name.startsWith(APP_PACKAGE) || activity !is ComponentActivity) return
            TapRecordingWindowCallback.install(activity, gaLogStore) { preferences.isGALogRecordingEnabled }
            DebugOverlay.attach(activity)
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
    }

    private const val APP_PACKAGE = "com.zion830.threedollars"
}
