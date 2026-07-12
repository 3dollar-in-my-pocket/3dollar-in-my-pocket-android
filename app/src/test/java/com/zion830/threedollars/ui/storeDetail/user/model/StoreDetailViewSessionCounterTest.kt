package com.zion830.threedollars.ui.storeDetail.user.model

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StoreDetailViewSessionCounterTest {

    @Before
    fun setUp() {
        StoreDetailViewSessionCounter.clear()
    }

    @Test
    fun increment_countsPerStoreId() {
        assertEquals(1, StoreDetailViewSessionCounter.increment(10))
        assertEquals(2, StoreDetailViewSessionCounter.increment(10))
        assertEquals(1, StoreDetailViewSessionCounter.increment(20))
        assertEquals(3, StoreDetailViewSessionCounter.increment(10))
    }
}
