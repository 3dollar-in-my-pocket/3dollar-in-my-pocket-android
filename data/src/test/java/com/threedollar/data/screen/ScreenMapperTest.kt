package com.threedollar.data.screen

import com.threedollar.common.serverdriven.model.SDCardModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.network.data.screen.StoreContributorCardResponse
import com.threedollar.network.data.screen.StoreContributorCursorResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import com.threedollar.network.data.screen.StoreContributorSectionResponse
import com.threedollar.network.data.screen.StoreContributorTextResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenMapperTest {

    @Test
    fun screenMapper_keepsHistoryCardsSeparatedEvenWhenTitlesMatch() {
        val response = StoreContributorScreenResponse(
            sections = listOf(
                StoreContributorSectionResponse(
                    type = "HISTORIES",
                    cards = listOf(
                        historyCardResponse(
                            cardId = "first-card",
                            title = "같은 닉네임",
                            subTitles = listOf("사진 2장 등록"),
                            metadata = "3시간 전",
                        ),
                        historyCardResponse(
                            cardId = "second-card",
                            title = "같은 닉네임",
                            subTitles = listOf("메뉴 수정", "메뉴 수정"),
                            metadata = "1시간 전",
                        ),
                    ),
                ),
            ),
        )

        val cards = (response.asModel().sections.single() as SDSectionModel.CardsSection).cards

        assertEquals(2, cards.size)
        assertTrue(cards.all { it is SDCardModel.HistoryCard })
        assertEquals(listOf("first-card", "second-card"), cards.map { it.cardId })
        assertEquals(
            listOf("사진 2장 등록"),
            (cards[0] as SDCardModel.HistoryCard).subTitles.map { it.text },
        )
        assertEquals(
            listOf("메뉴 수정", "메뉴 수정"),
            (cards[1] as SDCardModel.HistoryCard).subTitles.map { it.text },
        )
    }

    @Test
    fun historiesMapper_preservesIncomingCardOrderAndCursorWithoutNormalizing() {
        val response = StoreContributorHistoriesResponse(
            type = "HISTORIES",
            cards = listOf(
                historyCardResponse(
                    cardId = "alpha",
                    title = "같은 닉네임",
                    subTitles = listOf("운영 시간 제보"),
                    metadata = "어제",
                ),
                historyCardResponse(
                    cardId = "beta",
                    title = "같은 닉네임",
                    subTitles = listOf("사진 등록", "사진 등록"),
                    metadata = "오늘",
                ),
            ),
            cursor = StoreContributorCursorResponse(
                nextCursor = "next-page",
                hasMore = true,
            ),
        )

        val section = response.asCardsSectionModel()

        assertEquals(listOf("alpha", "beta"), section.cards.map { it.cardId })
        assertEquals(
            listOf("사진 등록", "사진 등록"),
            (section.cards[1] as SDCardModel.HistoryCard).subTitles.map { it.text },
        )
        assertEquals("next-page", section.cursor?.nextCursor)
        assertEquals(true, section.cursor?.hasMore)
    }

    private fun historyCardResponse(
        cardId: String,
        title: String,
        subTitles: List<String>,
        metadata: String,
    ): StoreContributorCardResponse = StoreContributorCardResponse(
        type = "HISTORY_CARD",
        cardId = cardId,
        title = StoreContributorTextResponse(text = title),
        subTitles = subTitles.map { StoreContributorTextResponse(text = it) },
        metadata = StoreContributorTextResponse(text = metadata),
    )
}
