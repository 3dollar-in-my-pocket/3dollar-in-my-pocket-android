package com.zion830.threedollars.ui.home.data

class HomeCurationAdEventGate {
    private var isLoaded = false
    private var isDisposed = false
    private var hasRecordedImpression = false

    fun onLoaded() {
        if (isDisposed) return
        isLoaded = true
        hasRecordedImpression = false
    }

    fun onFailed() {
        isLoaded = false
    }

    fun dispose() {
        isDisposed = true
        isLoaded = false
    }

    fun recordImpression(): Boolean {
        if (!isLoaded || isDisposed || hasRecordedImpression) return false
        hasRecordedImpression = true
        return true
    }

    fun recordClick(): Boolean = isLoaded && !isDisposed
}
