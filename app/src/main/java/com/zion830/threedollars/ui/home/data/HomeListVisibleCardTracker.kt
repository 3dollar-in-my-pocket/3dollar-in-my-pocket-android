package com.zion830.threedollars.ui.home.data

class HomeListVisibleCardTracker {
    private var visibleKeys: Set<String> = emptySet()

    fun update(keys: List<String>): List<String> {
        val entered = keys.filter { it !in visibleKeys }
        visibleKeys = keys.toSet()
        return entered
    }
}
