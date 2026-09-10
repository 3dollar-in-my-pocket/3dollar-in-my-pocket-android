package com.zion830.threedollars.ui.home.viewModel

import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListStoreReferenceModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeRequestCoordinatorTest {
    @Test
    fun `filter gate keeps only latest first page request`() {
        val gate = HomeFirstPageGate<String>()

        assertNull(gate.submit("A"))
        assertNull(gate.submit("B"))
        assertEquals("B", gate.resolve())
        assertNull(gate.resolve())
        assertEquals("C", gate.submit("C"))
    }

    @Test
    fun `new first page invalidates stale response and old pagination`() {
        val coordinator = HomeRequestCoordinator()
        val first = coordinator.beginFirstPage()
        val page = coordinator.beginNextPage(first, "cursor")
        val second = coordinator.beginFirstPage()

        assertFalse(coordinator.isCurrent(first))
        assertFalse(coordinator.isCurrent(page!!))
        assertTrue(coordinator.isCurrent(second))
    }

    @Test
    fun `same cursor cannot start twice until request finishes`() {
        val coordinator = HomeRequestCoordinator()
        val first = coordinator.beginFirstPage()
        val page = requireNotNull(coordinator.beginNextPage(first, "cursor"))

        assertNull(coordinator.beginNextPage(first, "cursor"))
        coordinator.finish(page)
        assertEquals("cursor", coordinator.beginNextPage(first, "cursor")?.cursor)
    }

    @Test
    fun `selection made while refresh is loading wins over request snapshot`() {
        val cards = listOf(card("A", 1), card("B", 2))

        assertEquals(
            "B",
            resolveHomeSelectedCardId(
                cards = cards,
                selectedCardIdAtStart = "A",
                latestSelectedCardId = "B",
                latestSelectedStoreId = 2,
                preserveSelectedStore = true,
            ),
        )
    }

    @Test
    fun `preserved store falls back by store reference when card id changes`() {
        val cards = listOf(card("new-id", 7), card("other", 8))

        assertEquals(
            "new-id",
            resolveHomeSelectedCardId(cards, "old-id", "old-id", 7, preserveSelectedStore = true),
        )
    }

    private fun card(cardId: String, storeId: Long) = HomeListCardModel.BasicCard(
        type = "BASIC_CARD",
        cardId = cardId,
        header = HomeListCardHeaderModel(),
        metadata = HomeListCardMetadataModel(),
        refs = listOf(HomeListStoreReferenceModel("STORE", storeId.toString(), "USER_STORE")),
    )
}
