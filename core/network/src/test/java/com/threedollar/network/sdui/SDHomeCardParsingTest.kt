package com.threedollar.network.sdui

import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.component.SDUnknownCardModel
import com.threedollar.network.sdui.core.gson.SDUIGson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SDHomeCardParsingTest {
    private fun card(name: String): SDCardModel {
        val json = requireNotNull(javaClass.classLoader?.getResource("sdui/home/$name")).readText()
        return SDUIGson.provideGson().fromJson(json, SDCardModel::class.java)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_실서버광고카드는_공통파서에서도_높이와로그를_보존한다`() {
        // Given
        val fixture = "HomeCurationAdMobCard.json"

        // When
        val model = card(fixture)

        // Then
        assertTrue(model is SDAdMobCardModel)
        val ad = model as SDAdMobCardModel
        assertEquals(50, ad.height)
        assertEquals("CLICK", ad.clickLog?.eventType)
        assertEquals("IMPRESSION", ad.impressionLog?.eventType)
        assertEquals("CURATION", ad.impressionLog?.extraParameters?.get("tabId"))
    }

    // TH-1401 TC8
    @Test
    fun `TH1401_TC8_실데이터의미지원카드타입은_공통파서에서_Unknown으로_떨어진다`() {
        // Given
        val fixture = "HomeCurationUnknownCard.json"

        // When
        val model = card(fixture)

        // Then
        assertTrue(model is SDUnknownCardModel)
        assertTrue(model.cardId.startsWith("ADMOB:"))
    }
}
