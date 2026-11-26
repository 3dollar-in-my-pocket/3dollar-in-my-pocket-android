package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListVisibleCardTrackerTest {

    // TH-1618 TC1
    @Test
    fun `TH1618_TC1_처음보이는카드만_노출대상으로_돌려준다`() {
        // Given
        val tracker = HomeListVisibleCardTracker()

        // When
        val first = tracker.update(listOf("ADMOB:1", "STORE:1", "STORE:2"))
        val scrolled = tracker.update(listOf("STORE:1", "STORE:2", "STORE:3"))

        // Then
        assertEquals(listOf("ADMOB:1", "STORE:1", "STORE:2"), first)
        assertEquals(listOf("STORE:3"), scrolled)
    }

    // TH-1618 TC2
    @Test
    fun `TH1618_TC2_화면밖으로나갔다가_다시들어오면_다시노출한다`() {
        // Given
        val tracker = HomeListVisibleCardTracker()
        tracker.update(listOf("ADMOB:1", "STORE:1"))
        tracker.update(listOf("STORE:1", "STORE:2"))

        // When
        val back = tracker.update(listOf("ADMOB:1", "STORE:1"))

        // Then
        assertEquals(listOf("ADMOB:1"), back)
    }

    // TH-1618 TC1
    @Test
    fun `TH1618_TC1_보이는카드가그대로면_다시보내지않는다`() {
        // Given
        val tracker = HomeListVisibleCardTracker()
        tracker.update(listOf("STORE:1", "STORE:2"))

        // When
        val same = tracker.update(listOf("STORE:1", "STORE:2"))

        // Then
        assertEquals(emptyList<String>(), same)
    }
}
