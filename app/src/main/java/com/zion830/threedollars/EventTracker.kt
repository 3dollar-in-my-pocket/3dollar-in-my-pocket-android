package com.zion830.threedollars

import android.os.Bundle
import android.util.Log

object EventTracker {

    private const val TAG = "EventTracker"

    @Deprecated(
        message = "Use LogManager.sendEvent() with typed events instead",
        replaceWith = ReplaceWith(
            "LogManager.sendEvent(ClickEvent(screen, objectType, objectId, params))",
            "com.threedollar.common.analytics.LogManager",
            "com.threedollar.common.analytics.ClickEvent"
        )
    )
    fun logEvent(name: String, params: Bundle? = null) {
        // 기존 방식 유지하되 내부적으로 FirebaseAnalytics 호출
        GlobalApplication.eventTracker.logEvent(name, params)

        // 디버그 로깅 추가 (레거시 이벤트 표시)
        if (BuildConfig.DEBUG) {
            val paramString = params?.keySet()?.joinToString(", ") { key ->
                "$key=${params.get(key)}"
            } ?: ""
            Log.d(TAG, "⚠️ Legacy event: $name | Params: { $paramString }")
        }
    }
}