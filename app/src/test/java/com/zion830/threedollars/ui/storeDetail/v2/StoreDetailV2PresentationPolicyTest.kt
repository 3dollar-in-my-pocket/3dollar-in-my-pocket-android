package com.zion830.threedollars.ui.storeDetail.v2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailV2PresentationPolicyTest {

    @Test
    fun `failed ad slot is removed while loading and loaded slots remain`() {
        assertTrue(StoreDetailAdLoadState.Loading.shouldRender)
        assertTrue(StoreDetailAdLoadState.Loaded.shouldRender)
        assertFalse(StoreDetailAdLoadState.Failed.shouldRender)
    }

    @Test
    fun `a later load failure does not remove an already loaded ad`() {
        assertEquals(
            StoreDetailAdLoadState.Failed,
            StoreDetailAdLoadState.Loading.afterLoadFailure(),
        )
        assertEquals(
            StoreDetailAdLoadState.Loaded,
            StoreDetailAdLoadState.Loaded.afterLoadFailure(),
        )
    }
}
