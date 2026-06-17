package com.zion830.threedollars.ui.home.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeSheetLayoutTest {

    @Test
    fun `location button sits above collapsed home sheet`() {
        assertEquals(
            HomeSheetLayout.COLLAPSED_PEEK_HEIGHT_DP + 16f,
            HomeSheetLayout.LOCATION_BUTTON_BOTTOM_MARGIN_DP,
            0f,
        )
    }
}
