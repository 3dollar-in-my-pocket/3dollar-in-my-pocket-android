package com.threedollar.data.screen

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * TH-1435 서버(SDUI) 로그가 매핑 과정에서 빠지지 않는지 검증한다. 픽스처는 dev 서버 실제 응답이다.
 */
class SDUILogMapperTest {

    private inline fun <reified T> loadData(name: String): T {
        val json = requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText()
        val data = JsonParser.parseString(json).asJsonObject.get("data")
        return Gson().fromJson(data, T::class.java)
    }

    // TH-1435 TC1
    @Test
    fun `TH1435_TC1_홈화면_viewLog의_preset을_그대로_매핑한다`() {
        // Given
        val response = loadData<HomeFilterScreenResponse>("HomeFilterScreenWithMapControl.json")

        // When
        val viewLog = response.asModel().viewLog

        // Then
        assertEquals("home", viewLog?.screenName)
        assertEquals("DEFAULT", viewLog?.extraParameters?.get("preset")?.anyValue)
    }

    // TH-1435 TC13
    @Test
    fun `TH1435_TC13_빈목록의_EMPTY_CARD는_impressionLog를_가진다`() {
        // Given
        val response = loadData<HomeListSectionResponse>("HomeListEmptyCard.json")

        // When
        val card = response.asModel().cards.single() as HomeListCardModel.EmptyCard

        // Then
        assertEquals("home", card.impressionLog?.screenName)
        assertEquals("card", card.impressionLog?.objectType)
        assertEquals("empty", card.impressionLog?.objectId)
    }

    // TH-1435 TC2
    @Test
    fun `TH1435_TC2_제보자화면_viewLog에_store_id가_담긴다`() {
        // Given
        val response = loadData<StoreContributorScreenResponse>("StoreContributorScreen.json")

        // When
        val viewLog = response.asModel().viewLog

        // Then
        assertEquals("store_contributors", viewLog?.screenName)
        assertEquals("139", viewLog?.extraParameters?.get("store_id")?.anyValue)
    }

    // TH-1435 TC16
    @Test
    fun `TH1435_TC16_제보자화면_수정버튼_actionBar의_clickLog를_매핑한다`() {
        // Given
        val response = loadData<StoreContributorScreenResponse>("StoreContributorScreen.json")

        // When
        val clickLog = response.asModel().sections
            .filterIsInstance<SDSectionModel.ActionBarSection>()
            .single()
            .actionBar
            .clickLog

        // Then
        assertEquals("store_contributors", clickLog?.screenName)
        assertEquals("button", clickLog?.objectType)
        assertEquals("edit", clickLog?.objectId)
    }
}
