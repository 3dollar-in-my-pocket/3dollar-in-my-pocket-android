package com.zion830.threedollars.ui.feed

import com.threedollar.domain.feed.model.FeedModel
import com.threedollar.domain.feed.model.FeedPageModel
import com.zion830.threedollars.ui.feed.model.FeedListUiState
import com.zion830.threedollars.ui.feed.model.FeedLocation
import com.zion830.threedollars.ui.feed.model.FeedTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class FeedListStateTest {

    private fun feed(id: String) = FeedModel(
        feedId = id,
        category = null,
        header = null,
        body = null,
        link = null,
        updatedAt = "2026-09-27T15:05:04",
    )

    private fun page(ids: IntRange, nextCursor: String?) = FeedPageModel(ids.map { feed(it.toString()) }, nextCursor)

    // TH-646 TC6
    @Test
    fun `TH646_TC6_다음페이지를_이어붙이고_커서가_없으면_더_요청하지_않는다`() {
        // Given
        val first = FeedListUiState().appendPage(page(1..20, nextCursor = "MjA="))

        // When
        val last = first.appendPage(page(21..25, nextCursor = null))

        // Then
        assertTrue(first.canLoadMore)
        assertEquals(25, last.feeds.size)
        assertFalse(last.canLoadMore)
    }

    // TH-646 TC6
    @Test
    fun `TH646_TC6_조회나_새로고침중에는_다음페이지를_요청하지_않는다`() {
        // Given
        val state = FeedListUiState().appendPage(page(1..20, nextCursor = "MjA="))

        // When
        val loading = state.copy(isLoading = true)
        val refreshing = state.copy(isRefreshing = true)

        // Then
        assertFalse(loading.canLoadMore)
        assertFalse(refreshing.canLoadMore)
    }

    // TH-646 TC7
    @Test
    fun `TH646_TC7_당겨서_새로고침하면_첫페이지로_목록을_교체한다`() {
        // Given
        val state = FeedListUiState().appendPage(page(1..20, nextCursor = "MjA=")).appendPage(page(21..40, nextCursor = "NDA="))

        // When
        val refreshed = state.copy(isRefreshing = true).replacePage(page(100..101, nextCursor = null))

        // Then
        assertEquals(listOf("100", "101"), refreshed.feeds.map { it.feedId })
        assertFalse(refreshed.isRefreshing)
        assertNull(refreshed.nextCursor)
    }

    // TH-646 TC9
    @Test
    fun `TH646_TC9_첫조회가_끝나고_0건이면_빈화면이다`() {
        // Given
        val initial = FeedListUiState(isLoading = true)

        // When
        val empty = initial.appendPage(page(IntRange.EMPTY, nextCursor = null))
        val loaded = initial.appendPage(page(1..1, nextCursor = null))

        // Then
        assertFalse(initial.isEmpty)
        assertTrue(empty.isEmpty)
        assertFalse(loaded.isEmpty)
    }

    // TH-646 TC13
    @Test
    fun `TH646_TC13_경과시간에_따라_방금전_분_시간_일_날짜로_나눈다`() {
        // Given
        val now = LocalDateTime.of(2026, 9, 27, 15, 0, 0)
        fun ago(dateTime: LocalDateTime) = FeedTime.of(dateTime.toString(), now)

        // When
        val results = listOf(
            ago(now.minusSeconds(30)),
            ago(now.minusMinutes(5)),
            ago(now.minusHours(3)),
            ago(now.minusDays(2)),
            ago(now.minusDays(3)),
        )

        // Then
        assertEquals(
            listOf(
                FeedTime.JustNow,
                FeedTime.Minutes(5),
                FeedTime.Hours(3),
                FeedTime.Days(2),
                FeedTime.Date(LocalDate.of(2026, 9, 24)),
            ),
            results,
        )
    }

    // TH-646 TC14
    @Test
    fun `TH646_TC14_홈_지도좌표가_없으면_기기위치로_대신하고_둘다_없으면_생략한다`() {
        // Given
        val device = 37.36 to 126.93
        val map = 37.50 to 127.02

        // When
        val withMap = FeedLocation.of(map = map, device = device)
        val withoutMap = FeedLocation.of(map = 0.0 to 0.0, device = device)
        val none = FeedLocation.of(map = null, device = null)

        // Then
        assertEquals(37.50, withMap.mapLatitude!!, 0.0)
        assertEquals(37.36, withMap.deviceLatitude!!, 0.0)
        assertEquals(37.36, withoutMap.mapLatitude!!, 0.0)
        assertNull(none.mapLatitude)
        assertNull(none.deviceLongitude)
    }
}
