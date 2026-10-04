package com.zion830.threedollars.debug

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/** 디버그 메뉴 토글·DEV 버튼 위치. 개발 빌드에만 있다. */
class DebugPreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var isShowStoreId: Boolean
        get() = preferences.getBoolean(KEY_SHOW_STORE_ID, false)
        set(value) = preferences.edit { putBoolean(KEY_SHOW_STORE_ID, value) }

    var isGALogRecordingEnabled: Boolean
        get() = preferences.getBoolean(KEY_GA_LOG_RECORDING, true)
        set(value) = preferences.edit { putBoolean(KEY_GA_LOG_RECORDING, value) }

    var isGALogToastEnabled: Boolean
        get() = preferences.getBoolean(KEY_GA_LOG_TOAST, false)
        set(value) = preferences.edit { putBoolean(KEY_GA_LOG_TOAST, value) }

    var isGALogImpressionToastEnabled: Boolean
        get() = preferences.getBoolean(KEY_GA_LOG_IMPRESSION_TOAST, false)
        set(value) = preferences.edit { putBoolean(KEY_GA_LOG_IMPRESSION_TOAST, value) }

    /** 화면 기준 비율(0~1). 저장한 적 없으면 null. */
    var floatingButtonPosition: Pair<Float, Float>?
        get() {
            val x = preferences.getFloat(KEY_BUTTON_X, -1f)
            val y = preferences.getFloat(KEY_BUTTON_Y, -1f)
            return if (x < 0f || y < 0f) null else x to y
        }
        set(value) = preferences.edit {
            putFloat(KEY_BUTTON_X, value?.first ?: -1f)
            putFloat(KEY_BUTTON_Y, value?.second ?: -1f)
        }

    private companion object {
        const val PREFERENCES_NAME = "debug_tools"
        const val KEY_SHOW_STORE_ID = "show_store_id"
        const val KEY_GA_LOG_RECORDING = "ga_log_recording"
        const val KEY_GA_LOG_TOAST = "ga_log_toast"
        const val KEY_GA_LOG_IMPRESSION_TOAST = "ga_log_impression_toast"
        const val KEY_BUTTON_X = "floating_button_x"
        const val KEY_BUTTON_Y = "floating_button_y"
    }
}
