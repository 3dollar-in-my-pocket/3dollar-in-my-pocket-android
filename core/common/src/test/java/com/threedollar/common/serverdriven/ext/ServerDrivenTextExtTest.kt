package com.threedollar.common.serverdriven.ext

import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerDrivenTextExtTest {

    @Test
    fun displayText_stripsHtmlTagsWhenTextIsHtml() {
        val text = SDTextModel(
            text = "<span style=\"font-size:20px; font-weight:700; color:#0F0F0F\">오소로</span>",
            isHtml = true,
        )

        assertEquals("오소로", text.displayText())
    }

    @Test
    fun toServerDrivenPlainText_stripsHtmlTagsEvenWithoutHtmlFlag() {
        val text = "#<span style=\"font-size:12px; color:#5A5A5A\">요거트아이스크림</span>"

        assertEquals("#요거트아이스크림", text.toServerDrivenPlainText())
    }

    @Test
    fun chipDisplayText_joinsMainTextAndAdditionalText() {
        val chip = SDChipModel(
            text = SDTextModel("영업중", isHtml = false),
            additionalText = SDTextModel("<span>1km</span>", isHtml = true),
        )

        assertEquals("영업중 1km", chip.displayText())
    }
}
