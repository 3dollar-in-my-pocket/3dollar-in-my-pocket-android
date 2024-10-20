package com.zion830.threedollars

import android.os.Bundle
import android.util.Log

object EventTracker {

    fun logEvent(name: String, params: Bundle? = null) {
        GlobalApplication.eventTracker.logEvent(name, params)
        Log.d("🟢GA", "[CUSTOM_EVENT]\nname: $name\nparams: ${params.toString()}")
    }
}