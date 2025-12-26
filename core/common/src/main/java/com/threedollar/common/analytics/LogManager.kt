package com.threedollar.common.analytics

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.threedollar.common.BuildConfig

interface LogManagerProtocol {
    fun sendPageView(screen: ScreenName, className: String)
    fun sendPageView(screen: ScreenName, className: String, extraParameters: Map<ParameterName, Any>)
    fun sendEvent(event: LogEvent)
}

object LogManager : LogManagerProtocol {
    private const val TAG = "LogManager"
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initialize(analytics: FirebaseAnalytics) {
        firebaseAnalytics = analytics
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "LogManager initialized")
        }
    }

    override fun sendPageView(screen: ScreenName, className: String) {
        sendPageView(screen, className, emptyMap())
    }

    override fun sendPageView(
        screen: ScreenName,
        className: String,
        extraParameters: Map<ParameterName, Any>
    ) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, className)
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screen.value)
            extraParameters.forEach { (key, value) ->
                putParameter(key, value)
            }
        }

        if (BuildConfig.DEBUG) {
            logDebugPageView(screen, className, extraParameters)
        }

        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun sendEvent(event: LogEvent) {
        val bundle = Bundle().apply {
            putString(ParameterName.SCREEN.value, event.screen.value)

            event.extraParameters?.forEach { (key, value) ->
                putParameter(key, value)
            }
        }

        if (BuildConfig.DEBUG) {
            logDebugCustomEvent(event)
        }

        firebaseAnalytics?.logEvent(event.name.value, bundle)
    }

    private fun Bundle.putParameter(key: ParameterName, value: Any) {
        when (value) {
            is String -> putString(key.value, value)
            is Int -> putInt(key.value, value)
            is Long -> putLong(key.value, value)
            is Double -> putDouble(key.value, value)
            is Boolean -> putBoolean(key.value, value)
            else -> putString(key.value, value.toString())
        }
    }

    private fun logDebugPageView(
        screen: ScreenName,
        className: String,
        params: Map<ParameterName, Any>
    ) {
        val paramString = if (params.isEmpty()) {
            ""
        } else {
            "\n" + params.entries.joinToString("\n") { (key, value) ->
                "\t${key.value}: $value,"
            }
        }

        Log.d(TAG, """🧡 [LogManager]: PageView
            => screen: ${screen.value}
            => type: $className
            => parameter: $paramString""")
    }

    private fun logDebugCustomEvent(event: LogEvent) {
        val paramString = event.extraParameters?.entries?.joinToString("\n") { (key, value) ->
            "\t${key.value}: $value,"
        } ?: ""

        val formattedParams = if (paramString.isNotEmpty()) {
            "\n$paramString"
        } else {
            ""
        }

        Log.d(TAG, """🧡 [LogManager]: CustomEvent
            => screen: ${event.screen.value}
            => name: ${event.name.value}
            => parameter:$formattedParams""")
    }
}
