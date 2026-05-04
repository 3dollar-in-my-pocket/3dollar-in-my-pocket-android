package com.threedollar.common.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.threedollar.common.serverdriven.model.SDClickLogModel

object SDClickLogger {
    private const val TAG = "LogManager"
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initialize(analytics: FirebaseAnalytics) {
        firebaseAnalytics = analytics
    }

    fun send(log: SDClickLogModel) {
        val bundle = Bundle().apply {
            putString(ParameterName.SCREEN.value, log.screenName)
            putString(ParameterName.OBJECT_ID.value, log.objectId)
            putString(ParameterName.OBJECT_TYPE.value, log.objectType)
            log.extraParameters.forEach { (key, value) ->
                value.anyValue?.let { raw ->
                    when (raw) {
                        is String -> putString(key, raw)
                        is Int -> putInt(key, raw)
                        is Long -> putLong(key, raw)
                        is Double -> putDouble(key, raw)
                        is Boolean -> putBoolean(key, raw)
                        else -> putString(key, raw.toString())
                    }
                }
            }
        }

        if (com.threedollar.common.BuildConfig.DEBUG) {
            val extras = log.extraParameters.entries.joinToString("\n") { (k, v) -> "\t$k: ${v.anyValue}," }
            Log.d(TAG, """🧡 [SDClickLog]
                => screen: ${log.screenName}
                => objectType: ${log.objectType}
                => objectId: ${log.objectId}
                => extras:
$extras""")
        }

        firebaseAnalytics?.logEvent(EventName.Click.value, bundle)
    }
}
