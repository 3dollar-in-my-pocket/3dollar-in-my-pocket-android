package com.threedollar.network.sdui

import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDSnsPostPlatform
import com.threedollar.common.sdui.model.section.SDStoreThreadPostSectionModel
import com.threedollar.common.sdui.model.section.SDUnknownSectionModel
import com.threedollar.network.sdui.core.gson.SDUIGson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SDStoreThreadPostParsingTest {

    private val gson = SDUIGson.provideGson()

    private val threadPostSectionJson = """
        {
          "type": "THREADS_POST",
          "sectionId": "THREADS_POST",
          "style": { "backgroundColor": "#FFFFFF" },
          "header": { "title": { "text": "쓰레드 소식", "isHtml": false, "fontColor": "#0F0F0F" } },
          "cards": [
            {
              "permalink": "https://www.threads.com/@gyeol_jeong84/post/DOaaa",
              "clickLog": { "screenName": "store_detail", "objectType": "card", "objectId": "threads_post", "extraParameters": { "store_id": "12806182" } }
            }
          ],
          "impressionLog": { "screenName": "store_detail", "objectType": "carousel", "objectId": "threads_post", "extraParameters": { "store_id": "12806182" } }
        }
    """.trimIndent()

    private val snsPostSectionJson = """
        {
          "type": "THREADS_POST",
          "sectionId": "SNS_POST",
          "style": { "backgroundColor": "#FFFFFF" },
          "header": { "title": { "text": "SNS에서 발견한 이 가게", "isHtml": false, "fontColor": "#0F0F0F" } },
          "cards": [
            {
              "platform": "THREADS",
              "permalink": "https://www.threads.com/@gyeol_jeong84/post/DdN8dCMGHR9",
              "clickLog": { "screenName": "store_detail", "objectType": "card", "objectId": "sns_post", "extraParameters": { "store_id": "12806182", "store_type": "USER_STORE", "sns_platform": "THREADS" }, "eventType": "CLICK" }
            },
            {
              "platform": "INSTAGRAM",
              "permalink": "https://www.instagram.com/p/DPaaa",
              "clickLog": { "screenName": "store_detail", "objectType": "card", "objectId": "sns_post", "extraParameters": { "store_id": "12806182", "store_type": "USER_STORE", "sns_platform": "INSTAGRAM" }, "eventType": "CLICK" }
            },
            {
              "platform": "TIKTOK",
              "permalink": "https://www.tiktok.com/@someone/video/1",
              "clickLog": { "screenName": "store_detail", "objectType": "card", "objectId": "sns_post", "extraParameters": { "store_id": "12806182", "store_type": "USER_STORE", "sns_platform": "TIKTOK" }, "eventType": "CLICK" }
            }
          ],
          "impressionLog": { "screenName": "store_detail", "objectType": "carousel", "objectId": "sns_post", "extraParameters": { "store_id": "12806182", "store_type": "USER_STORE" }, "eventType": "IMPRESSION" }
        }
    """.trimIndent()

    private fun parse(vararg sections: String): SDStoreScreenModel = gson.fromJson(
        """{ "viewLog": { "screenName": "store_detail" }, "sections": [${sections.joinToString(",")}] }""",
        SDStoreScreenModel::class.java
    )

    // TH-1449 TC2
    @Test
    fun `TH1449_TC2_THREADS_POST응답을_파싱하면_섹션필드와_카드가_파싱된다`() {
        // Given
        val json = threadPostSectionJson

        // When
        val section = parse(json).sections.orEmpty().single() as SDStoreThreadPostSectionModel

        // Then
        assertEquals(SDSectionType.THREADS_POST, section.type)
        assertEquals("THREADS_POST", section.sectionId)
        assertEquals("#FFFFFF", section.style?.backgroundColor)
        assertEquals("쓰레드 소식", section.header?.title?.text)
        assertEquals(listOf("https://www.threads.com/@gyeol_jeong84/post/DOaaa"), section.permalinks)
        assertEquals("threads_post", section.cards?.single()?.clickLog?.objectId)
        assertEquals("carousel", section.impressionLog?.objectType)
        assertNull(section.embeds)
    }

    // TH-1449 TC12
    @Test
    fun `TH1449_TC12_알수없는_섹션타입이_섞이면_해당섹션만_UNKNOWN이되고_나머지는_파싱된다`() {
        // Given
        val unknownJson = """{ "type": "SOMETHING_NEW", "sectionId": "NEW" }"""

        // When
        val sections = parse(unknownJson, threadPostSectionJson).sections.orEmpty()

        // Then
        assertTrue(sections[0] is SDUnknownSectionModel)
        assertTrue(sections[1] is SDStoreThreadPostSectionModel)
    }

    // TH-1512 TC1
    @Test
    fun `TH1512_TC1_카드의_platform이_THREADS와_INSTAGRAM이면_각_플랫폼으로_파싱된다`() {
        // Given
        val json = snsPostSectionJson

        // When
        val section = parse(json).sections.orEmpty().single() as SDStoreThreadPostSectionModel

        // Then
        assertEquals(
            listOf(SDSnsPostPlatform.THREADS, SDSnsPostPlatform.INSTAGRAM),
            section.supportedCards.map { it.snsPlatform }
        )
        assertEquals(listOf("https://www.threads.com/@gyeol_jeong84/post/DdN8dCMGHR9"), section.threadsPermalinks)
        assertEquals("INSTAGRAM", section.supportedCards[1].clickLog?.extraParameters?.get("sns_platform"))
    }

    // TH-1512 TC2
    @Test
    fun `TH1512_TC2_알수없는_platform값이면_해당카드만_빠지고_섹션은_파싱된다`() {
        // Given
        val json = snsPostSectionJson

        // When
        val section = parse(json).sections.orEmpty().single() as SDStoreThreadPostSectionModel

        // Then
        assertEquals(3, section.cards?.size)
        assertNull(section.cards?.get(2)?.snsPlatform)
        assertEquals(
            listOf("https://www.threads.com/@gyeol_jeong84/post/DdN8dCMGHR9", "https://www.instagram.com/p/DPaaa"),
            section.permalinks
        )
    }

    // TH-1512 TC3
    @Test
    fun `TH1512_TC3_platform필드가_없는_카드는_쓰레드로_본다`() {
        // Given
        val json = threadPostSectionJson

        // When
        val section = parse(json).sections.orEmpty().single() as SDStoreThreadPostSectionModel

        // Then
        assertEquals(SDSnsPostPlatform.THREADS, section.cards?.single()?.snsPlatform)
        assertEquals(listOf("https://www.threads.com/@gyeol_jeong84/post/DOaaa"), section.threadsPermalinks)
    }
}
