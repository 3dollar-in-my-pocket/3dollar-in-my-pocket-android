package com.threedollar.domain.store.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDisplayItemModelTest {

    @Test
    fun storeDisplayItemTypeFrom_returnsKnownType() {
        assertEquals(
            StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL,
            StoreDisplayItemType.from("DISAPPEARANCE_INQUIRY_MODAL"),
        )
        assertEquals(
            StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL,
            StoreDisplayItemType.from("VISIT_CERTIFICATION_INDUCEMENT_MODAL"),
        )
    }

    @Test
    fun storeDisplayItemTypeFrom_returnsUnknownForUnexpectedValue() {
        assertEquals(StoreDisplayItemType.UNKNOWN, StoreDisplayItemType.from("NEW_MODAL"))
        assertEquals(StoreDisplayItemType.UNKNOWN, StoreDisplayItemType.from(null))
    }

    @Test
    fun sessionViewCountRangeContains_isInclusive() {
        val range = SessionViewCountRangeModel(min = 2, max = 4)

        assertFalse(range.contains(1))
        assertTrue(range.contains(2))
        assertTrue(range.contains(4))
        assertFalse(range.contains(5))
    }

    @Test
    fun sessionViewCountRangeContains_allowsOpenEndedRange() {
        assertTrue(SessionViewCountRangeModel(min = 2).contains(10))
        assertFalse(SessionViewCountRangeModel(min = 2).contains(1))
        assertTrue(SessionViewCountRangeModel(max = 2).contains(1))
        assertFalse(SessionViewCountRangeModel(max = 2).contains(3))
    }
}
