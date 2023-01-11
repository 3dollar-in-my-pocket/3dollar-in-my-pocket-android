package com.zion830.threedollars

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object EventTracker {

    fun logEvent(name: String, params: Bundle? = null) {
        GlobalApplication.eventTracker.logEvent(name, params)
    }
}