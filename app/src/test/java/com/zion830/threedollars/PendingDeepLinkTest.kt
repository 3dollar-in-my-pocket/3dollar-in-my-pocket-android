package com.zion830.threedollars

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PendingDeepLinkTest {

    @After
    fun tearDown() {
        PendingDeepLink.clear()
    }

    @Test
    fun `TH1070_TC1_링크를_연_직후_세션이_만료되면_그_링크를_돌려준다`() {
        PendingDeepLink.record("dollars-dev://myCoupons", now = 1_000L)

        assertEquals("dollars-dev://myCoupons", PendingDeepLink.takeIfRecent(now = 1_000L + 5_000L))
    }

    @Test
    fun `TH1070_TC2_유효시간이_지난_링크는_돌려주지_않는다`() {
        PendingDeepLink.record("dollars-dev://store?storeId=527060", now = 1_000L)

        assertNull(PendingDeepLink.takeIfRecent(now = 1_000L + PendingDeepLink.VALID_MILLIS + 1L))
    }

    @Test
    fun `TH1070_TC3_한번_꺼낸_링크는_다시_돌려주지_않는다`() {
        PendingDeepLink.record("dollars-dev://myCoupons", now = 1_000L)
        PendingDeepLink.takeIfRecent(now = 2_000L)

        assertNull(PendingDeepLink.takeIfRecent(now = 3_000L))
    }

    @Test
    fun `TH1070_TC4_기록이_없으면_null`() {
        assertNull(PendingDeepLink.takeIfRecent(now = 1_000L))
    }
}
