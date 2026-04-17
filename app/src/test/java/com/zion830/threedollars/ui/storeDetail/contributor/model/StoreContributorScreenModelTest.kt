package com.zion830.threedollars.ui.storeDetail.contributor.model

import com.threedollar.common.serverdriven.model.SDCardModel
import com.threedollar.common.serverdriven.model.SDCursorModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StoreContributorScreenModelTest {

    @Test
    fun appendFirstCardsSection_appendsIncomingCardsWithoutMergingDuplicateTitles() {
        val initialScreen = SDScreenModel(
            sections = listOf(
                SDSectionModel.Unknown(type = "HEADER"),
                SDSectionModel.CardsSection(
                    type = "HISTORIES",
                    cards = listOf(
                        historyCard(
                            cardId = "first-card",
                            title = "같은 닉네임",
                            subTitles = listOf("사진 등록"),
                        ),
                    ),
                    cursor = SDCursorModel(nextCursor = "cursor-1", hasMore = true),
                ),
                SDSectionModel.Unknown(type = "ACTION_BAR"),
            ),
        )
        val nextPage = SDSectionModel.CardsSection(
            type = "HISTORIES",
            cards = listOf(
                historyCard(
                    cardId = "second-card",
                    title = "같은 닉네임",
                    subTitles = listOf("메뉴 수정"),
                ),
            ),
            cursor = SDCursorModel(nextCursor = "cursor-2", hasMore = false),
        )

        val updatedScreen = initialScreen.appendFirstCardsSection(nextPage)
        val cardsSection = updatedScreen.sections.firstCardsSection()

        assertNotNull(cardsSection)
        assertEquals(3, updatedScreen.sections.size)
        assertEquals(
            listOf("first-card", "second-card"),
            cardsSection!!.cards.filterIsInstance<SDCardModel.HistoryCard>().map { it.cardId },
        )
        assertEquals("cursor-2", cardsSection.cursor?.nextCursor)
        assertEquals(false, cardsSection.cursor?.hasMore)
    }

    private fun historyCard(
        cardId: String,
        title: String,
        subTitles: List<String>,
    ): SDCardModel.HistoryCard = SDCardModel.HistoryCard(
        type = "HISTORY_CARD",
        cardId = cardId,
        title = SDTextModel(text = title, isHtml = false),
        subTitles = subTitles.map { SDTextModel(text = it, isHtml = false) },
    )
}
