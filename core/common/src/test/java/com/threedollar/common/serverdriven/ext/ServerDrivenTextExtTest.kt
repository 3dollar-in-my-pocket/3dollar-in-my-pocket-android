package com.threedollar.common.serverdriven.ext

import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ServerDrivenTextExtTest {

    @Test
    fun styledSegments_inheritNestedStylesAndRestoreParentAfterClosingTag() {
        val text = SDTextModel(
            "<span style='color:#FFFFFF;font-size:16px'>공식 <span style='color:#FF0000;font-weight:700'>인증</span> 가게</span>!",
            true,
            fontColor = "#000000",
            fontWeight = "400",
        )
        assertEquals(
            listOf(
                SDStyledTextSegment("공식 ", 16, 400, "#FFFFFF"),
                SDStyledTextSegment("인증", 16, 700, "#FF0000"),
                SDStyledTextSegment(" 가게", 16, 400, "#FFFFFF"),
                SDStyledTextSegment("!", null, 400, "#000000"),
            ),
            text.styledSegments(),
        )
    }

    @Test
    fun styledSegments_preserveLineBreakBoldAndDecodeEntitiesOnce() {
        val text = SDTextModel("<span>A<b>B</b><br/>C &amp;lt; &#x1F35E;</span>", true)
        assertEquals("AB\nC &lt; 🍞", text.styledSegments().joinToString("") { it.text })
        assertEquals(700, text.styledSegments().first { it.text == "B" }.fontWeight)
        assertEquals("&lt;", "&amp;lt;".toServerDrivenPlainText())
    }

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

    @Test
    fun styledSegments_parseActualServerSpanCssAndDecodeEntities() {
        val text = SDTextModel(
            text = """
                <span style="font-size:16px; font-weight:700; color:#0F0F0F">가게 정보 &amp; 메뉴</span><span style="font-size:16px; font-weight:400; color:#787878">2개</span>
            """.trimIndent(),
            isHtml = true,
            fontColor = "#000000",
        )

        val segments = text.styledSegments()

        assertEquals(2, segments.size)
        assertEquals("가게 정보 & 메뉴", segments[0].text)
        assertEquals(16, segments[0].fontSizePx)
        assertEquals(700, segments[0].fontWeight)
        assertEquals("#0F0F0F", segments[0].fontColor)
        assertEquals("2개", segments[1].text)
        assertEquals(400, segments[1].fontWeight)
        assertEquals("#787878", segments[1].fontColor)
    }

    @Test
    fun styledSegments_plainTextFallsBackToTopLevelStyle() {
        val text = SDTextModel(
            text = "일반 텍스트",
            isHtml = false,
            fontColor = "#123456",
            fontWeight = "600",
        )

        assertEquals(
            listOf(
                SDStyledTextSegment(
                    text = "일반 텍스트",
                    fontSizePx = null,
                    fontWeight = 600,
                    fontColor = "#123456",
                )
            ),
            text.styledSegments(),
        )
    }
}
