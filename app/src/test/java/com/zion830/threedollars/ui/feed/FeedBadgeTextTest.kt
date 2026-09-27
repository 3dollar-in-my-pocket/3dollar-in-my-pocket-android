package com.zion830.threedollars.ui.feed

import com.threedollar.common.sdui.model.element.SDTextModel
import com.zion830.threedollars.ui.feed.model.FeedBadgeText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FeedBadgeTextTest {

    private fun text(value: String, isHtml: Boolean = false) = SDTextModel(text = value, isHtml = isHtml, fontColor = "#FFFFFF")

    // TH-1404 TC1
    @Test
    fun `TH1404_TC1_배지문구_앞의_이모지를_떼어_아이콘으로_그린다`() {
        // Given
        val names = listOf(
            "📢 새로운 가게 소식이 올라왔어요!",
            "🙅 영업을 종료했어요!",
            "❤️ 새로운 리뷰가 올라왔어요!",
            "🙌 지금 뜨는 가게!",
        )

        // When
        val parts = names.map { FeedBadgeText.split(text(it)) }

        // Then
        assertEquals(listOf("📢", "🙅", "❤️", "🙌"), parts.map { it.leadingEmoji })
        assertEquals(
            listOf("새로운 가게 소식이 올라왔어요!", "영업을 종료했어요!", "새로운 리뷰가 올라왔어요!", "지금 뜨는 가게!"),
            parts.map { it.text.text },
        )
        assertEquals("#FFFFFF", parts.first().text.fontColor)
    }

    // TH-1404 TC1
    @Test
    fun `TH1404_TC1_이모지로_시작하지_않거나_HTML이면_그대로_둔다`() {
        // Given
        val plain = text("새로운 가게 소식")
        val html = text("<b>📢 새 소식</b>", isHtml = true)
        val emojiOnly = text("📢")

        // When
        val results = listOf(plain, html, emojiOnly).map { FeedBadgeText.split(it) }

        // Then
        results.forEach { assertNull(it.leadingEmoji) }
        assertEquals(listOf(plain, html, emojiOnly), results.map { it.text })
    }
}
