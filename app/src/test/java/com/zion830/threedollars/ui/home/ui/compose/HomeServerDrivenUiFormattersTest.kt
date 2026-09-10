package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.ui.text.font.FontWeight
import com.threedollar.common.serverdriven.model.SDTextModel
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

    @Test
    fun `home title keeps raw html and entities for shared renderer`() {
        val raw = "<span style=\"color:#FF0000;font-weight:700\">가게&amp;이름</span>"
        val title = SDTextModel(text = raw, isHtml = true)

        val rendered = title.homeTitleForRendering(addBreakOpportunities = true)

        assertEquals(raw, rendered.text)
        assertEquals(true, rendered.isHtml)
    }

    @Test
    fun `plain long title retains break opportunities`() {
        val rendered = SDTextModel(text = "긴제목", isHtml = false)
            .homeTitleForRendering(addBreakOpportunities = true)

        assertEquals("긴\u200B제\u200B목", rendered.text)
    }
}
