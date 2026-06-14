package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeServerDrivenUiFormattersTest {

    @Test
    fun toServerDrivenFontWeight_mapsServerFontWeightAliases() {
        assertEquals(FontWeight.Normal, "NORMAL".toServerDrivenFontWeight(FontWeight.Bold))
        assertEquals(FontWeight.Normal, "400".toServerDrivenFontWeight(FontWeight.Bold))
        assertEquals(FontWeight.SemiBold, "SEMI_BOLD".toServerDrivenFontWeight(FontWeight.Normal))
        assertEquals(FontWeight.SemiBold, "600".toServerDrivenFontWeight(FontWeight.Normal))
        assertEquals(FontWeight.Bold, "BOLD".toServerDrivenFontWeight(FontWeight.Normal))
        assertEquals(FontWeight.Bold, "700".toServerDrivenFontWeight(FontWeight.Normal))
    }
}
