package com.threedollar.common.listener

import android.os.Bundle

interface EventTrackerListener {
    fun logEvent(name: String, params: Bundle? = null)
}