package com.threedollar.network.sdui

import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStoreInfoV2SectionModel
import com.threedollar.network.sdui.core.gson.SDUIGson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SDStoreSectionLogParsingTest {

    private val gson = SDUIGson.provideGson()

    private fun load(name: String): SDStoreScreenModel {
        val json = requireNotNull(javaClass.classLoader?.getResource("sdui/store-v2/$name")).readText()
        return gson.fromJson(json, SDStoreScreenModel::class.java)
    }

    // TH-1467 TC4
    @Test
    fun `TH1467_TC4_섹션별_노출로그를_서버값그대로_파싱한다`() {
        // Given
        val screen = load("StoreScreenV2WithLogs.json")

        // When
        val coupon = screen.sections.orEmpty().first { it.type == SDSectionType.COUPON }

        // Then
        val log = requireNotNull(coupon.impressionLog)
        assertEquals("store_detail", log.screenName)
        assertEquals("section", log.objectType)
        assertEquals("store_section", log.objectId)
        assertEquals("COUPON", log.extraParameters?.get("section_type"))
    }

    // TH-1467 TC4
    @Test
    fun `TH1467_TC4_노출로그가없는섹션은_null로_파싱한다`() {
        // Given
        val screen = load("StoreScreenV2WithLogs.json")

        // When
        val margin = screen.sections.orEmpty().first { it.type == SDSectionType.MARGIN }

        // Then
        assertNull(margin.impressionLog)
    }

    // TH-1467 TC5
    @Test
    fun `TH1467_TC5_정보섹션링크행의_클릭로그를_파싱한다`() {
        // Given
        val screen = load("StoreScreenV2WithLogs.json")

        // When
        val infoV2 = screen.sections.orEmpty().filterIsInstance<SDStoreInfoV2SectionModel>().first()
        val linkRow = infoV2.detailCard?.rows.orEmpty()
            .first { it.type == SDStoreInfoV2SectionModel.DetailRowType.LINK }

        // Then
        val log = requireNotNull(linkRow.clickLog)
        assertEquals("button", log.objectType)
        assertEquals("sns_link", log.objectId)
        assertEquals("120009", log.extraParameters?.get("store_id"))
    }
}
