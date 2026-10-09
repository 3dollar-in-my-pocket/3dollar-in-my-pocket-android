package com.zion830.threedollars.ui.home.data

data class HomeCurationQuerySnapshot(
    val location: HomeCurationRequestLocation,
    val revision: Long,
)

object HomeCurationQueryPolicy {
    fun commit(
        previous: HomeCurationQuerySnapshot?,
        location: HomeCurationRequestLocation,
        refresh: Boolean,
    ): HomeCurationQuerySnapshot {
        if (previous != null && !refresh) return previous
        return HomeCurationQuerySnapshot(location, revision = (previous?.revision ?: 0L) + 1L)
    }

    fun canReuse(
        state: HomeCurationUiState,
        snapshot: HomeCurationQuerySnapshot,
        requestedRevision: Long?,
        tabId: String,
    ): Boolean = requestedRevision == snapshot.revision &&
        state.requestLocation == snapshot.location &&
        state.sectionTabId == tabId &&
        (state.isLoading || (state.section != null && state.errorMessage == null))
}
