package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeImpressionGatesTest {

    // TH-1467 TC1, TC2
    @Test
    fun `TH1467_TC2_섹션이보이면_받은노출로그를_바로보낸다`() {
        // Given
        val gate = PendingSectionImpression<String>()

        // When
        val sent = gate.offer("home_list", isVisible = true)

        // Then
        assertEquals("home_list", sent)
        assertNull(gate.flush(isVisible = true))
    }

    // TH-1467 TC1, TC2
    @Test
    fun `TH1467_TC2_안보일때받은노출로그는_보이게될때_한번만보낸다`() {
        // Given
        val gate = PendingSectionImpression<String>()

        // When
        val sentWhileHidden = gate.offer("home_list", isVisible = false)
        val stillHidden = gate.flush(isVisible = false)
        val firstVisible = gate.flush(isVisible = true)
        val secondVisible = gate.flush(isVisible = true)

        // Then
        assertNull(sentWhileHidden)
        assertNull(stillHidden)
        assertEquals("home_list", firstVisible)
        assertNull(secondVisible)
    }

    // TH-1467 TC1
    @Test
    fun `TH1467_TC1_새로조회를시작하면_보류중인노출로그를_버린다`() {
        // Given
        val gate = PendingSectionImpression<String>()
        gate.offer("home_curation", isVisible = false)

        // When
        gate.clear()

        // Then
        assertNull(gate.flush(isVisible = true))
    }

    // TH-1467 TC1, TC2
    @Test
    fun `TH1467_TC2_로그가없으면_보내지도_보류하지도않는다`() {
        // Given
        val gate = PendingSectionImpression<String>()

        // When
        val sent = gate.offer(null, isVisible = true)

        // Then
        assertNull(sent)
        assertNull(gate.flush(isVisible = true))
    }

    // TH-1467 TC3
    @Test
    fun `TH1467_TC3_같은캐러셀의같은카드는_한번만보낸다`() {
        // Given
        val dedup = HomeCurationCardImpressionDedup()

        // When
        val first = dedup.shouldSend("POPULAR_SNACKS", "S:47")
        val second = dedup.shouldSend("POPULAR_SNACKS", "S:47")
        val otherCarousel = dedup.shouldSend("TASTE_SNACKS", "S:47")

        // Then
        assertTrue(first)
        assertFalse(second)
        assertTrue(otherCarousel)
    }

    // TH-1467 TC3
    @Test
    fun `TH1467_TC3_카테고리를바꿔새로받은캐러셀만_다시보낼수있다`() {
        // Given
        val dedup = HomeCurationCardImpressionDedup()
        dedup.shouldSend("POPULAR_SNACKS", "S:47")
        dedup.shouldSend("TASTE_SNACKS", "S:525168")

        // When
        dedup.reset("POPULAR_SNACKS")

        // Then
        assertTrue(dedup.shouldSend("POPULAR_SNACKS", "S:47"))
        assertFalse(dedup.shouldSend("TASTE_SNACKS", "S:525168"))
    }

    // TH-1467 TC3
    @Test
    fun `TH1467_TC3_섹션을새로받으면_모든카드를_다시보낼수있다`() {
        // Given
        val dedup = HomeCurationCardImpressionDedup()
        dedup.shouldSend("POPULAR_SNACKS", "S:47")

        // When
        dedup.resetAll()

        // Then
        assertTrue(dedup.shouldSend("POPULAR_SNACKS", "S:47"))
    }
}
