package com.zion830.threedollars.ui.home.data

internal class HomePageViewCoordinator {
    private var resolvedEvent: HomePageViewEvent? = null
    private var pendingEntries = 0

    fun resolve(event: HomePageViewEvent): List<HomePageViewEvent> {
        resolvedEvent = event
        if (pendingEntries == 0) return emptyList()
        return List(pendingEntries) { event }.also { pendingEntries = 0 }
    }

    fun onEntry(): HomePageViewEvent? {
        resolvedEvent?.let { return it }
        pendingEntries += 1
        return null
    }
}
