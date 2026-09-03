package com.threedollar.common.compose.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ComposeColorUtilsTest {

    @Test
    fun `server rgba hex is converted to Android argb`() {
        assertEquals(
            "#99181818",
            "#18181899".normalizeServerDrivenColor(),
        )
    }

    @Test
    fun `server rgb hex keeps opaque color`() {
        assertEquals(
            "#FF858F",
            "#FF858F".normalizeServerDrivenColor(),
        )
    }
}
