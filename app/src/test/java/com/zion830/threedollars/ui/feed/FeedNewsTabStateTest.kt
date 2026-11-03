package com.zion830.threedollars.ui.feed

import com.threedollar.domain.feed.model.FeedModel
import com.threedollar.domain.feed.model.FeedPageModel
import com.zion830.threedollars.ui.feed.model.FeedListUiState
import com.zion830.threedollars.ui.feed.model.FeedLocation
import com.zion830.threedollars.ui.feed.model.FeedNewBadgePolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedNewsTabStateTest {

    private val seoul = FeedLocation.of(map = 37.5665 to 126.9780, device = 37.5665 to 126.9780)
    private val gangnam = FeedLocation.of(map = 37.4979 to 127.0276, device = 37.5665 to 126.9780)

    private fun feed(id: String) = FeedModel(
        feedId = id,
        category = null,
        header = null,
        body = null,
        link = null,
        updatedAt = "2026-10-31T15:05:04",
    )

    private fun page(vararg ids: String, nextCursor: String? = null) = FeedPageModel(ids.map(::feed), nextCursor)

    private fun pageOf(range: IntRange, nextCursor: String?) = FeedPageModel(range.map { feed("%024d".format(it)) }, nextCursor)

    private fun loadedAt(location: FeedLocation, revision: Long, vararg ids: String): FeedListUiState =
        FeedListUiState().changeLocation(location, revision).startReload().appendPage(page(*ids))

    // TH-1427 TC2
    @Test
    fun `TH1427_TC2_소식탭을_처음열면_지도좌표_기준으로_목록을_불러온다`() {
        // Given
        val state = FeedListUiState().changeLocation(seoul, revision = 1L)

        // When
        val selected = state.selectTab(true)
        val loading = selected.startReload()

        // Then
        assertTrue(selected.needsReload)
        assertEquals(1L, loading.listRevision)
        assertEquals(37.5665, loading.location?.mapLatitude!!, 0.0)
        assertFalse(loading.needsReload)
    }

    // TH-1427 TC3
    @Test
    fun `TH1427_TC3_20개를_넘으면_커서로_다음20개를_이어붙인다`() {
        // Given
        val first = FeedListUiState().changeLocation(seoul, 1L).startReload().appendPage(pageOf(1..20, nextCursor = "MjA="))

        // When
        val next = first.copy(isLoading = true).appendPage(pageOf(21..40, nextCursor = null))

        // Then
        assertTrue(first.canLoadMore)
        assertEquals(40, next.feeds.size)
        assertFalse(next.canLoadMore)
    }

    // TH-1427 TC4
    @Test
    fun `TH1427_TC4_지도를_옮겨_재검색하면_새좌표로_목록을_비우고_다시_불러온다`() {
        // Given
        val loaded = loadedAt(seoul, 1L, "b", "a").selectTab(true)

        // When
        val moved = loaded.changeLocation(gangnam, revision = 2L)
        val reloading = moved.startReload()

        // Then
        assertTrue(moved.needsReload)
        assertTrue(reloading.feeds.isEmpty())
        assertTrue(reloading.showsSkeleton)
        assertEquals(2L, reloading.listRevision)
        assertEquals(37.4979, reloading.location?.mapLatitude!!, 0.0)
    }

    // TH-1427 TC5
    @Test
    fun `TH1427_TC5_주소검색으로_핀이_갱신되면_같은좌표라도_새_리비전으로_다시_불러온다`() {
        // Given
        val loaded = loadedAt(seoul, 1L, "a")

        // When
        val sameRevision = loaded.changeLocation(seoul, revision = 1L)
        val searched = loaded.changeLocation(seoul, revision = 2L)

        // Then
        assertFalse(sameRevision.needsReload)
        assertTrue(searched.needsReload)
    }

    // TH-1427 TC6
    @Test
    fun `TH1427_TC6_소식이_0건이면_빈상태이고_실패가_아니다`() {
        // Given
        val loading = FeedListUiState().changeLocation(seoul, 1L).startReload()

        // When
        val empty = loading.appendPage(page())

        // Then
        assertTrue(empty.isEmpty)
        assertFalse(empty.isFailed)
        assertFalse(empty.showsSkeleton)
    }

    // TH-1427 TC7
    @Test
    fun `TH1427_TC7_첫조회가_실패하면_실패상태가_되고_다시시도하면_다시_불러온다`() {
        // Given
        val loading = FeedListUiState().changeLocation(seoul, 1L).startReload()

        // When
        val failed = loading.failPage()
        val retried = failed.startReload()

        // Then
        assertTrue(failed.isFailed)
        assertFalse(failed.showsSkeleton)
        assertTrue(failed.needsReload)
        assertFalse(retried.isFailed)
        assertTrue(retried.showsSkeleton)
    }

    // TH-1427 TC7
    @Test
    fun `TH1427_TC7_다음페이지_실패는_목록을_유지하고_실패화면으로_바꾸지_않는다`() {
        // Given
        val loaded = FeedListUiState().changeLocation(seoul, 1L).startReload().appendPage(pageOf(1..20, nextCursor = "MjA="))

        // When
        val failed = loaded.copy(isLoading = true).failPage()

        // Then
        assertFalse(failed.isFailed)
        assertEquals(20, failed.feeds.size)
        assertFalse(failed.isLoading)
    }

    // TH-1427 TC8
    @Test
    fun `TH1427_TC8_불러오는중이면_스켈레톤을_보여준다`() {
        // Given
        val state = FeedListUiState().changeLocation(seoul, 1L)

        // When
        val loading = state.startReload()
        val nextPageLoading = loading.appendPage(page("a", nextCursor = "MQ==")).copy(isLoading = true)

        // Then
        assertTrue(loading.showsSkeleton)
        assertFalse(nextPageLoading.showsSkeleton)
    }

    // TH-1427 TC10
    @Test
    fun `TH1427_TC10_다른탭에서_마지막으로_본것보다_새소식이_있으면_점을_보여준다`() {
        // Given
        val state = FeedListUiState(lastSeenFeedId = "6ab7616420134fcca11e43a6").changeLocation(seoul, 1L)

        // When
        val updated = state.applyLatestFeedId("6ab8b210dd25a23c2db26a1e")

        // Then
        assertTrue(updated.showsNewBadge)
        assertEquals("6ab7616420134fcca11e43a6", updated.lastSeenFeedId)
    }

    // TH-1427 TC11
    @Test
    fun `TH1427_TC11_소식탭을_열면_점이_사라지고_최신feedId를_저장한다`() {
        // Given
        val badge = FeedListUiState(lastSeenFeedId = "6ab7616420134fcca11e43a6").applyLatestFeedId("6ab8b210dd25a23c2db26a1e")

        // When
        val opened = badge.selectTab(true)

        // Then
        assertTrue(badge.showsNewBadge)
        assertFalse(opened.showsNewBadge)
        assertEquals("6ab8b210dd25a23c2db26a1e", opened.lastSeenFeedId)
    }

    // TH-1427 TC11
    @Test
    fun `TH1427_TC11_탭이_선택된동안_새소식이_와도_점없이_본것으로_저장한다`() {
        // Given
        val opened = FeedListUiState(lastSeenFeedId = "a").selectTab(true)

        // When
        val reloaded = opened.changeLocation(gangnam, 2L).startReload().appendPage(page("c", "b"))

        // Then
        assertFalse(reloaded.showsNewBadge)
        assertEquals("c", reloaded.latestFeedId)
        assertEquals("c", reloaded.lastSeenFeedId)
    }

    // TH-1427 TC12
    @Test
    fun `TH1427_TC12_본소식_이후_새소식이_없으면_다시열어도_점이_없다`() {
        // Given
        val restored = FeedListUiState(lastSeenFeedId = "6ab8b210dd25a23c2db26a1e")

        // When
        val sameLatest = restored.applyLatestFeedId("6ab8b210dd25a23c2db26a1e")
        val olderLatest = restored.applyLatestFeedId("6ab7616420134fcca11e43a6")

        // Then
        assertFalse(sameLatest.showsNewBadge)
        assertFalse(olderLatest.showsNewBadge)
    }

    // TH-1427 TC12
    @Test
    fun `TH1427_TC12_다른동네의_오래된소식을_열어도_마지막으로_본feedId를_낮추지_않는다`() {
        // Given
        val seen = FeedListUiState(lastSeenFeedId = "6ab8b210dd25a23c2db26a1e")

        // When
        val opened = seen.applyLatestFeedId("6ab7616420134fcca11e43a6").selectTab(true)

        // Then
        assertEquals("6ab8b210dd25a23c2db26a1e", opened.lastSeenFeedId)
    }

    // TH-1427 TC13
    @Test
    fun `TH1427_TC13_한번도_열지않았으면_소식이_1개이상일때만_점을_보여준다`() {
        // Given
        val firstUse = FeedListUiState(lastSeenFeedId = null)

        // When
        val withFeed = firstUse.applyLatestFeedId("6ab7616420134fcca11e43a6")
        val withoutFeed = firstUse.applyLatestFeedId(null)

        // Then
        assertTrue(withFeed.showsNewBadge)
        assertFalse(withoutFeed.showsNewBadge)
        assertTrue(FeedNewBadgePolicy.isVisible(isTabSelected = false, latestFeedId = "a", lastSeenFeedId = null))
        assertFalse(FeedNewBadgePolicy.isVisible(isTabSelected = true, latestFeedId = "a", lastSeenFeedId = null))
    }

    // TH-1427 TC17
    @Test
    fun `TH1427_TC17_위치권한이_없으면_지도기본위치를_지도좌표와_기기좌표로_쓴다`() {
        // Given
        val defaultMap = 37.5665 to 126.9780

        // When
        val location = FeedLocation.of(map = defaultMap, device = null)

        // Then
        assertEquals(37.5665, location.mapLatitude!!, 0.0)
        assertEquals(126.9780, location.mapLongitude!!, 0.0)
        assertEquals(37.5665, location.deviceLatitude!!, 0.0)
    }

    // TH-1427 TC17
    @Test
    fun `TH1427_TC17_좌표를_받기전에는_목록을_불러오지_않는다`() {
        // Given
        val state = FeedListUiState()

        // When
        val selected = state.selectTab(true)

        // Then
        assertNull(selected.location)
        assertFalse(selected.needsReload)
    }
}
