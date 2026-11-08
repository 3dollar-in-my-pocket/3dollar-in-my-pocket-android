package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.network.data.screen.HomeListSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeListCardStoreInfoMapperTest {
    private val gson = Gson()

    private fun load(name: String): HomeListSectionResponse = gson.fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        HomeListSectionResponse::class.java,
    )

    private fun HomeListSectionResponse.basicCard(cardId: String): HomeListCardModel.BasicCard =
        asModel().cards.filterIsInstance<HomeListCardModel.BasicCard>().first { it.cardId == cardId }

    // TH-1589 TC1
    @Test
    fun `TH1589_TC1_찜하지않은_가게카드면_storeInfo가_있고_isSubscriber는_false다`() {
        // Given
        val response = load("HomeListSectionWithAdditionalInfos.json")

        // When
        val storeInfo = response.basicCard("S:120027").storeInfo

        // Then
        assertEquals("120027", storeInfo?.storeId)
        assertEquals("USER_STORE", storeInfo?.storeType)
        assertFalse(requireNotNull(storeInfo).isSubscriber)
    }

    // TH-1589 TC2
    @Test
    fun `TH1589_TC2_isSubscriber가_true인_가게카드면_찜상태로_매핑된다`() {
        // Given
        val response = load("HomeListSectionWithAdditionalInfosVariants.json")

        // When
        val storeInfo = response.basicCard("S:120027").storeInfo

        // Then
        assertTrue(requireNotNull(storeInfo).isSubscriber)
    }

    // TH-1589 TC11
    @Test
    fun `TH1589_TC11_additionalInfos가_EMPTY거나_없으면_storeInfo는_null이다`() {
        // Given
        val response = load("HomeListSectionWithAdditionalInfosVariants.json")

        // When
        val emptyType = response.basicCard("S:120009").storeInfo
        val missing = response.basicCard("S:525128").storeInfo

        // Then
        assertNull(emptyType)
        assertNull(missing)
    }

    // TH-1589 TC11
    @Test
    fun `TH1589_TC11_storeId가_비어있으면_storeInfo는_null이다`() {
        // Given
        val response = gson.fromJson(
            """
            {
              "cards": [
                {
                  "type": "BASIC_CARD",
                  "cardId": "S:1",
                  "header": { "title": { "text": "가게" } },
                  "metadata": { "primary": [], "secondary": [] },
                  "marker": {
                    "focused": { "text": { "text": "" } },
                    "unfocused": { "text": { "text": "" } },
                    "location": { "latitude": 37.1, "longitude": 127.2 }
                  },
                  "additionalInfos": { "type": "STORE", "isSubscriber": true, "storeId": "" }
                }
              ]
            }
            """.trimIndent(),
            HomeListSectionResponse::class.java,
        )

        // When
        val storeInfo = response.basicCard("S:1").storeInfo

        // Then
        assertNull(storeInfo)
    }

    @Test
    fun `회귀_알수없는_storeType이어도_크래시없이_원문값으로_매핑된다`() {
        // Given
        val response = load("HomeListSectionWithAdditionalInfosVariants.json")

        // When
        val storeInfo = response.basicCard("S:12806245").storeInfo

        // Then
        assertEquals("12806245", storeInfo?.storeId)
        assertEquals("UNKNOWN_STORE", storeInfo?.storeType)
    }
}
