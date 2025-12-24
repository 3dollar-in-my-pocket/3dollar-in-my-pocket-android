package com.zion830.threedollars

import android.os.Bundle
import com.threedollar.common.listener.EventTrackerListener
import javax.inject.Inject

class EventTrackerImpl @Inject constructor() : EventTrackerListener {

    @Deprecated(
        message = "Use LogManager.sendEvent() with typed events instead",
        replaceWith = ReplaceWith(
            "LogManager.sendEvent(event)",
            "com.threedollar.common.analytics.LogManager"
        )
    )
    override fun logEvent(name: String, params: Bundle?) {
        EventTracker.logEvent(name, params)
    }
}