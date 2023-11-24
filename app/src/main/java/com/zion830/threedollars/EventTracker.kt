package com.zion830.threedollars

import android.os.Bundle

object EventTracker {

    fun logEvent(name: String, params: Bundle? = null) {
        GlobalApplication.eventTracker.logEvent(name, params)
    }
}