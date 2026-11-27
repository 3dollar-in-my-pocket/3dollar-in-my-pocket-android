package com.zion830.threedollars.ui.feed

import com.threedollar.common.sdui.model.element.SDLinkType
import com.zion830.threedollars.ui.feed.FeedNewsFixtures.adMobCard
import com.zion830.threedollars.ui.feed.FeedNewsFixtures.newsCard
import com.zion830.threedollars.ui.feed.FeedNewsFixtures.pageOf
import com.zion830.threedollars.ui.feed.model.FeedCardImpressionDedup
import com.zion830.threedollars.ui.feed.model.FeedImageViewerRequest
import com.zion830.threedollars.ui.feed.model.FeedListUiState
import com.zion830.threedollars.ui.feed.model.FeedLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedNewsSectionStateTest {

    private val gunpo = FeedLocation.of(map = 37.3617 to 126.9352, device = 37.3617 to 126.9352)

    private fun loading() = FeedListUiState().changeLocation(gunpo, 1L).selectTab(true, newsTabId = "NEWS").startReload()

    // TH-1559 TC1
    @Test
    fun `TH1559_TC1_소식섹션을_받으면_서버헤더와_items를_서버순서대로_든다`() {
        // Given
        val page = pageOf(listOf(newsCard("c"), newsCard("a"), newsCard("b")), nextCursor = null)

        // When
        val loaded = loading().appendPage(page)

        // Then
        assertEquals(FeedNewsFixtures.header, loaded.header)
        assertEquals(listOf("NEWS:c", "NEWS:a", "NEWS:b"), loaded.items.map { it.cardId })
    }

    // TH-1559 TC1
    @Test
    fun `TH1559_TC1_지도를_옮겨_다시불러오는동안에도_이전헤더를_유지한다`() {
        // Given
        val loaded = loading().appendPage(pageOf(listOf(newsCard("a")), nextCursor = null))

        // When
        val reloading = loaded.changeLocation(FeedLocation.of(map = 37.5 to 127.0, device = null), 2L).startReload()

        // Then
        assertEquals(FeedNewsFixtures.header, reloading.header)
        assertTrue(reloading.items.isEmpty())
    }

    // TH-1559 TC3
    @Test
    fun `TH1559_TC3_광고카드는_서버가_준자리에만_있고_앱이_끼워넣지않는다`() {
        // Given
        val serverItems = listOf(adMobCard(), newsCard("b"), newsCard("a"))

        // When
        val loaded = loading().appendPage(pageOf(serverItems, nextCursor = null))
        val noAd = loading().appendPage(pageOf(listOf(newsCard("b")), nextCursor = null))

        // Then
        assertEquals(serverItems, loaded.items)
        assertEquals(listOf("NEWS:b"), noAd.items.map { it.cardId })
        assertEquals(50, loaded.adMobCard("ADMOB:00f3ad1c")?.card?.height)
    }

    // TH-1559 TC4
    @Test
    fun `TH1559_TC4_다음페이지는_nextCursor로_이어붙이고_이미받은카드는_다시붙이지않는다`() {
        // Given
        val first = loading().appendPage(pageOf(listOf(adMobCard(), newsCard("3"), newsCard("2")), nextCursor = "MjAyNjExMjYxMDMwMDA"))

        // When
        val next = first.copy(isLoading = true).appendPage(pageOf(listOf(newsCard("2"), newsCard("1")), nextCursor = null))

        // Then
        assertEquals("MjAyNjExMjYxMDMwMDA", first.nextCursor)
        assertTrue(first.canLoadMore)
        assertEquals(listOf("ADMOB:00f3ad1c", "NEWS:3", "NEWS:2", "NEWS:1"), next.items.map { it.cardId })
        assertFalse(next.canLoadMore)
    }

    // TH-1559 TC4
    @Test
    fun `TH1559_TC4_hasMore가_false면_nextCursor가_있어도_더_불러오지않는다`() {
        // Given
        val page = pageOf(listOf(newsCard("a")), nextCursor = "MjA=").copy(hasMore = false)

        // When
        val loaded = loading().appendPage(page)

        // Then
        assertNull(loaded.nextCursor)
        assertFalse(loaded.canLoadMore)
    }

    // TH-1559 TC5
    @Test
    fun `TH1559_TC5_소식카드를_찾으면_서버link와_clickLog를_그대로_돌려준다`() {
        // Given
        val loaded = loading().appendPage(pageOf(listOf(adMobCard(), newsCard("12806248")), nextCursor = null))

        // When
        val card = loaded.contentCard("NEWS:12806248")
        val adAsContent = loaded.contentCard("ADMOB:00f3ad1c")

        // Then
        assertEquals(SDLinkType.APP_SCHEME, card?.link?.type)
        assertEquals("/store?storeId=12806248&view=collapsed", card?.link?.link)
        assertEquals("CLICK", card?.clickLog?.eventType)
        assertNull(adAsContent)
    }

    // TH-1559 TC6
    @Test
    fun `TH1559_TC6_카드노출로그는_목록조회마다_카드당_한번만_보낸다`() {
        // Given
        val dedup = FeedCardImpressionDedup()

        // When
        val first = dedup.shouldSend("NEWS:a")
        val again = dedup.shouldSend("NEWS:a")
        val ad = dedup.shouldSend("ADMOB:00f3ad1c")
        dedup.reset()
        val afterReload = dedup.shouldSend("NEWS:a")

        // Then
        assertTrue(first)
        assertFalse(again)
        assertTrue(ad)
        assertTrue(afterReload)
    }

    // TH-1559 TC8
    @Test
    fun `TH1559_TC8_본문사진을_누르면_그블록사진을_누른사진부터_연다`() {
        // Given
        val card = newsCard("a", imageUrls = listOf("https://img/1.jpeg", "https://img/2.jpeg"))

        // When
        val request = FeedImageViewerRequest.of(card, blockIndex = 0, imageIndex = 1)
        val outOfRange = FeedImageViewerRequest.of(card, blockIndex = 0, imageIndex = 2)
        val noBlock = FeedImageViewerRequest.of(card, blockIndex = 1, imageIndex = 0)

        // Then
        assertEquals(listOf("https://img/1.jpeg", "https://img/2.jpeg"), request?.imageUrls)
        assertEquals(1, request?.startIndex)
        assertNull(outOfRange)
        assertNull(noBlock)
    }

    // TH-1559 TC9
    @Test
    fun `TH1559_TC9_items가_비어있으면_빈상태다`() {
        // Given
        val state = loading()

        // When
        val empty = state.appendPage(pageOf(emptyList(), nextCursor = null))

        // Then
        assertFalse(state.isEmpty)
        assertTrue(empty.isEmpty)
        assertFalse(empty.isFailed)
    }
}
