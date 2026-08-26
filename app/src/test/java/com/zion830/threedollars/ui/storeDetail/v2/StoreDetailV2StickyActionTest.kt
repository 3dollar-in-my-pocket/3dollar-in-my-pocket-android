package com.zion830.threedollars.ui.storeDetail.v2

import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailV2StickyActionTest {

    @Test
    fun `sticky actions appear as soon as preview action row leaves viewport`() {
        assertEquals(
            false,
            shouldShowStoreDetailStickyActions(
                hasActions = true,
                previewIndex = 0,
                firstVisibleItemIndex = 0,
                previewActionsVisible = true,
            ),
        )
        assertEquals(
            true,
            shouldShowStoreDetailStickyActions(
                hasActions = true,
                previewIndex = 0,
                firstVisibleItemIndex = 0,
                previewActionsVisible = false,
            ),
        )
        assertEquals(
            true,
            shouldShowStoreDetailStickyActions(
                hasActions = true,
                previewIndex = 0,
                firstVisibleItemIndex = 1,
                previewActionsVisible = true,
            ),
        )
    }
}
