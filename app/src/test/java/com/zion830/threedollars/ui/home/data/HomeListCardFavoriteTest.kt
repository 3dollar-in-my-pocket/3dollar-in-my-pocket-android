package com.zion830.threedollars.ui.home.data

import com.threedollar.common.R as CommonR
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListCardStoreInfoModel
import com.threedollar.common.serverdriven.model.HomeListMarkerModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeListCardFavoriteTest {

    // TH-1589 TC3
    @Test
    fun `TH1589_TC3_찜등록에_성공하면_해당가게카드가_찜상태가되고_추가토스트를_고른다`() {
        // Given
        val cards = listOf(card(storeId = "1", isSubscriber = false))

        // When
        val updated = HomeListCardFavorite.withFavorite(cards, storeId = "1", isFavorite = true)
        val toast = HomeListCardFavorite.toastMessageRes(isFavorite = true)

        // Then
        assertTrue(updated.basicCard(0).storeInfo!!.isSubscriber)
        assertEquals(CommonR.string.store_detail_favorite_added, toast)
    }

    // TH-1589 TC4
    @Test
    fun `TH1589_TC4_찜해제에_성공하면_해당가게카드가_미찜상태가되고_삭제토스트를_고른다`() {
        // Given
        val cards = listOf(card(storeId = "1", isSubscriber = true))

        // When
        val updated = HomeListCardFavorite.withFavorite(cards, storeId = "1", isFavorite = false)
        val toast = HomeListCardFavorite.toastMessageRes(isFavorite = false)

        // Then
        assertFalse(updated.basicCard(0).storeInfo!!.isSubscriber)
        assertEquals(CommonR.string.store_detail_favorite_removed, toast)
    }

    // TH-1589 TC6
    @Test
    fun `TH1589_TC6_미찜카드를_탭하면_로그_value는_on이고_가게id와_타입이_담긴다`() {
        // Given
        val storeInfo = HomeListCardStoreInfoModel(storeId = "120027", storeType = "USER_STORE", isSubscriber = false)

        // When
        val params = HomeListCardFavorite.clickLogParams(storeInfo)

        // Then
        assertEquals(
            mapOf(
                ParameterName.STORE_ID to "120027",
                ParameterName.STORE_TYPE to "USER_STORE",
                ParameterName.VALUE to "on",
            ),
            params,
        )
    }

    // TH-1589 TC6
    @Test
    fun `TH1589_TC6_찜카드를_탭하면_로그_value는_off다`() {
        // Given
        val storeInfo = HomeListCardStoreInfoModel(storeId = "120027", storeType = "BOSS_STORE", isSubscriber = true)

        // When
        val params = HomeListCardFavorite.clickLogParams(storeInfo)

        // Then
        assertEquals("off", params[ParameterName.VALUE])
        assertEquals("BOSS_STORE", params[ParameterName.STORE_TYPE])
    }

    // TH-1589 TC6
    @Test
    fun `TH1589_TC6_storeType이_없으면_store_type_파라미터를_넣지않는다`() {
        // Given
        val storeInfo = HomeListCardStoreInfoModel(storeId = "1", storeType = null, isSubscriber = false)

        // When
        val params = HomeListCardFavorite.clickLogParams(storeInfo)

        // Then
        assertFalse(params.containsKey(ParameterName.STORE_TYPE))
    }

    // TH-1589 TC8
    @Test
    fun `TH1589_TC8_같은가게_요청이_끝나기전에는_다시_시작할수없고_끝나면_다시_시작된다`() {
        // Given
        val gate = HomeListFavoriteRequestGate()

        // When
        val first = gate.tryStart("1")
        val duplicated = gate.tryStart("1")
        val otherStore = gate.tryStart("2")
        gate.finish("1")
        val afterFinish = gate.tryStart("1")

        // Then
        assertTrue(first)
        assertFalse(duplicated)
        assertTrue(otherStore)
        assertTrue(afterFinish)
    }

    // TH-1589 TC10
    @Test
    fun `TH1589_TC10_찜을_바꾸면_같은가게카드만_바뀌고_다른카드는_그대로다`() {
        // Given
        val target = card(cardId = "S:1", storeId = "1", isSubscriber = false)
        val other = card(cardId = "S:2", storeId = "2", isSubscriber = false)
        val withoutStoreInfo = card(cardId = "S:3", storeId = null, isSubscriber = false)
        val empty = HomeListCardModel.EmptyCard(type = "EMPTY_CARD", cardId = "E:1")

        // When
        val updated = HomeListCardFavorite.withFavorite(
            listOf(target, other, withoutStoreInfo, empty),
            storeId = "1",
            isFavorite = true,
        )

        // Then
        assertTrue(updated.basicCard(0).storeInfo!!.isSubscriber)
        assertSame(other, updated[1])
        assertSame(withoutStoreInfo, updated[2])
        assertSame(empty, updated[3])
    }

    // TH-1589 TC10
    @Test
    fun `TH1589_TC10_다음페이지를_붙여도_바꾼_찜상태가_유지된다`() {
        // Given
        val firstPage = HomeListCardFavorite.withFavorite(
            listOf(card(cardId = "S:1", storeId = "1", isSubscriber = false)),
            storeId = "1",
            isFavorite = true,
        )
        val nextPage = listOf(
            card(cardId = "S:1", storeId = "1", isSubscriber = false),
            card(cardId = "S:2", storeId = "2", isSubscriber = false),
        )

        // When
        val merged = (firstPage + nextPage).distinctBy { it.cardId }

        // Then
        assertEquals(2, merged.size)
        assertTrue(merged.basicCard(0).storeInfo!!.isSubscriber)
        assertFalse(merged.basicCard(1).storeInfo!!.isSubscriber)
    }

    private fun List<HomeListCardModel>.basicCard(index: Int): HomeListCardModel.BasicCard =
        this[index] as HomeListCardModel.BasicCard

    private fun card(
        cardId: String = "S:1",
        storeId: String?,
        isSubscriber: Boolean,
    ): HomeListCardModel.BasicCard = HomeListCardModel.BasicCard(
        type = "BASIC_CARD",
        cardId = cardId,
        header = HomeListCardHeaderModel(title = SDTextModel(text = "가게", isHtml = false)),
        metadata = HomeListCardMetadataModel(),
        marker = HomeListMarkerModel(
            focused = SDChipModel(text = SDTextModel(text = "", isHtml = false)),
            unfocused = SDChipModel(text = SDTextModel(text = "", isHtml = false)),
            location = SDLocationModel(latitude = 37.1, longitude = 127.2),
        ),
        storeInfo = storeId?.let {
            HomeListCardStoreInfoModel(storeId = it, storeType = "USER_STORE", isSubscriber = isSubscriber)
        },
    )
}
