package com.threedollar.common.sdui.text

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SDHtmlTextTest {

    @Test
    fun `span 의 font-size, font-weight, color 를 구간 스타일로 읽는다`() {
        // Given
        val html = "<span style=\"font-size:20px; font-weight:700; color:#0F0F0F\">가게명</span>"

        // When
        val runs = SDHtmlText.parse(html)

        // Then
        assertEquals(listOf(SDTextRun("가게명", fontSize = 20f, fontWeight = 700, color = "#0F0F0F")), runs)
    }

    @Test
    fun `여러 span 은 각자의 스타일을 가진 구간으로 나뉜다`() {
        // Given
        val html = "<span style=\"font-size:16px; font-weight:700; color:#0F0F0F\">가게 사진 </span>" +
            "<span style=\"font-size:16px; font-weight:400; color:#0F0F0F\">3개</span>"

        // When
        val runs = SDHtmlText.parse(html)

        // Then
        assertEquals(2, runs.size)
        assertEquals(SDTextRun("가게 사진 ", 16f, 700, "#0F0F0F"), runs[0])
        assertEquals(SDTextRun("3개", 16f, 400, "#0F0F0F"), runs[1])
    }

    @Test
    fun `엔티티를 디코딩하고 br 은 줄바꿈으로 바꾼다`() {
        // Given
        val html = "<span style=\"color:#0F0F0F\">가게 정보 &amp; 메뉴<br/>두 번째 줄 &#39;끝&#x27;</span>"

        // When
        val text = SDHtmlText.plainText(html)

        // Then
        assertEquals("가게 정보 & 메뉴\n두 번째 줄 '끝'", text)
    }

    @Test
    fun `b 태그는 굵게 처리하고 닫힌 뒤에는 바깥 스타일로 돌아간다`() {
        // Given
        val html = "<span style=\"color:#111111\">앞<b>굵게</b>뒤</span>"

        // When
        val runs = SDHtmlText.parse(html)

        // Then
        assertEquals(
            listOf(
                SDTextRun("앞", color = "#111111"),
                SDTextRun("굵게", fontWeight = 700, color = "#111111"),
                SDTextRun("뒤", color = "#111111")
            ),
            runs
        )
    }

    @Test
    fun `태그가 없는 문자열은 스타일 없는 한 구간이다`() {
        // Given
        val html = "그냥 텍스트"

        // When
        val runs = SDHtmlText.parse(html)

        // Then
        assertEquals(listOf(SDTextRun("그냥 텍스트")), runs)
    }

    @Test
    fun `알 수 없는 태그나 잘못된 스타일 값은 무시하고 내용은 남긴다`() {
        // Given
        val html = "<div><span style=\"font-size:abc; font-weight:heavy\">내용</span></div>"

        // When
        val runs = SDHtmlText.parse(html)

        // Then
        assertEquals(listOf(SDTextRun("내용")), runs)
    }

    @Test
    fun `빈 문자열이나 null 이면 빈 목록을 돌려준다`() {
        assertTrue(SDHtmlText.parse(null).isEmpty())
        assertTrue(SDHtmlText.parse("").isEmpty())
    }
}
