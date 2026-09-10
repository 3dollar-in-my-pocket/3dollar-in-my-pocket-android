package com.zion830.threedollars.ui.home.viewModel

import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.zion830.threedollars.ui.home.data.storePreviewStoreIdOrNull

internal class HomeFirstPageGate<T> {
    private var resolved = false
    private var pending: T? = null

    fun submit(request: T): T? {
        if (resolved) return request
        pending = request
        return null
    }

    fun resolve(): T? {
        if (resolved) return null
        resolved = true
        return pending.also { pending = null }
    }
}

internal data class HomeRequestToken(
    val generation: Long,
    val cursor: String?,
)

internal class HomeRequestCoordinator {
    private var generation = 0L
    private var inFlightCursor: String? = null

    fun beginFirstPage(): HomeRequestToken {
        generation += 1
        inFlightCursor = null
        return HomeRequestToken(generation = generation, cursor = null)
    }

    fun beginNextPage(parent: HomeRequestToken, cursor: String): HomeRequestToken? {
        if (!isCurrent(parent) || cursor.isBlank() || inFlightCursor == cursor) return null
        inFlightCursor = cursor
        return HomeRequestToken(generation = generation, cursor = cursor)
    }

    fun isCurrent(token: HomeRequestToken): Boolean = token.generation == generation

    fun finish(token: HomeRequestToken) {
        if (isCurrent(token) && token.cursor == inFlightCursor) inFlightCursor = null
    }
}

internal fun resolveHomeSelectedCardId(
    cards: List<HomeListCardModel.BasicCard>,
    selectedCardIdAtStart: String?,
    latestSelectedCardId: String?,
    latestSelectedStoreId: Long?,
    preserveSelectedStore: Boolean,
): String? {
    val selectionChangedWhileLoading = latestSelectedCardId != selectedCardIdAtStart
    return when {
        selectionChangedWhileLoading && cards.any { it.cardId == latestSelectedCardId } -> latestSelectedCardId
        preserveSelectedStore -> cards.firstOrNull { it.cardId == latestSelectedCardId }?.cardId
            ?: latestSelectedStoreId?.let { storeId ->
                cards.firstOrNull { it.storePreviewStoreIdOrNull() == storeId }?.cardId
            }
            ?: cards.firstOrNull()?.cardId
        else -> cards.firstOrNull()?.cardId
    }
}
