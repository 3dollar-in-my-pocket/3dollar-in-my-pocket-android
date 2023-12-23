package com.zion830.threedollars

import android.os.Bundle
import com.threedollar.common.listener.EventTrackerListener

class EventTrackerImpl : EventTrackerListener {

    override fun logEvent(name: String, params: Bundle? = null) {
        EventTracker.logEvent(name, params)
    }
}