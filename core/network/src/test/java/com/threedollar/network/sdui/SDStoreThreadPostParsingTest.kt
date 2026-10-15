package com.threedollar.network.sdui

import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDSectionType
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
}
