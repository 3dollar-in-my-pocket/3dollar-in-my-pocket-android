package com.zion830.threedollars

import android.os.Bundle
import com.threedollar.common.listener.EventTrackerListener
import javax.inject.Inject

class EventTrackerImpl @Inject constructor() : EventTrackerListener {

    override fun logEvent(name: String, params: Bundle?) {
        EventTracker.logEvent(name, params)
    }
}