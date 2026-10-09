package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeCurationQueryPolicyTest {

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_첫위치확정은_첫조회조건을_만든다`() {
        // Given
        val location = HomeCurationTestFixtures.location

        // When
        val snapshot = HomeCurationQueryPolicy.commit(previous = null, location = location, refresh = false)
        val refreshed = HomeCurationQueryPolicy.commit(previous = null, location = location, refresh = true)

        // Then
        assertEquals(location, snapshot.location)
        assertEquals(1L, snapshot.revision)
        assertEquals(location, refreshed.location)
        assertEquals(1L, refreshed.revision)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_지도이동만으로는_확정조회조건과_캐시를_바꾸지_않는다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val previous = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 3L)
        val pannedLocation = previous.location.copy(mapLatitude = 37.56, mapLongitude = 126.97)

        // When
        val snapshot = HomeCurationQueryPolicy.commit(previous, pannedLocation, refresh = false)
        val canReuse = HomeCurationQueryPolicy.canReuse(loaded, snapshot, requestedRevision = 3L, tabId = "CURATION")

        // Then
        assertSame(previous, snapshot)
        assertEquals(HomeCurationTestFixtures.location, snapshot.location)
        assertTrue(canReuse)
    }

    // TH-1401 TC11
    @Test
    fun `TH1401_TC11_필터변경의_비강제조회는_기존큐레이션조건과_캐시를_유지한다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val previous = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 5L)
        val currentMapLocation = previous.location.copy(mapLatitude = 37.57)

        // When
        val snapshot = HomeCurationQueryPolicy.commit(previous, currentMapLocation, refresh = false)
        val canReuse = HomeCurationQueryPolicy.canReuse(loaded, snapshot, requestedRevision = 5L, tabId = "CURATION")

        // Then
        assertSame(previous, snapshot)
        assertTrue(canReuse)
    }

    // TH-1401 TC11
    @Test
    fun `TH1401_TC11_지도조건을_확정하지_않고_탭을_왕복하면_큐레이션캐시를_재사용한다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val storeList = HomeCurationStateReducer.selectTab(loaded, "DEFAULT")
        val returned = HomeCurationStateReducer.selectTab(storeList, "CURATION")
        val previous = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 6L)
        val currentMapLocation = previous.location.copy(mapLongitude = 126.96)

        // When
        val snapshot = HomeCurationQueryPolicy.commit(previous, currentMapLocation, refresh = false)
        val canReuse = HomeCurationQueryPolicy.canReuse(returned, snapshot, requestedRevision = 6L, tabId = "CURATION")

        // Then
        assertTrue(canReuse)
        assertSame(previous, snapshot)
        assertSame(loaded.carousels, returned.carousels)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_명시적위치확정은_새조회조건과_세대로_캐시를_무효화한다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val previous = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 7L)
        val location = previous.location.copy(mapLatitude = 37.57, mapLongitude = 126.96)

        // When
        val snapshot = HomeCurationQueryPolicy.commit(previous, location, refresh = true)
        val canReuse = HomeCurationQueryPolicy.canReuse(loaded, snapshot, requestedRevision = 7L, tabId = "CURATION")

        // Then
        assertEquals(location, snapshot.location)
        assertEquals(8L, snapshot.revision)
        assertFalse(canReuse)
    }

    @Test
    fun `회귀_주변목록에서_동일좌표를_명시적으로_재검색해도_큐레이션캐시가_무효화된다`() {
        // Given
        val storeList = HomeCurationStateReducer.selectTab(HomeCurationTestFixtures.loadedState(), "DEFAULT")
        val previous = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 9L)

        // When
        val snapshot = HomeCurationQueryPolicy.commit(previous, previous.location, refresh = true)
        val canReuse = HomeCurationQueryPolicy.canReuse(storeList, snapshot, requestedRevision = 9L, tabId = "CURATION")

        // Then
        assertNotSame(previous, snapshot)
        assertEquals(previous.location, snapshot.location)
        assertEquals(10L, snapshot.revision)
        assertFalse(canReuse)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_첫응답도_진행중인요청도_없으면_캐시를_재사용하지_않는다`() {
        // Given
        val snapshot = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 1L)
        val initial = HomeCurationUiState(
            sectionTabId = "CURATION",
            requestLocation = snapshot.location,
        )

        // When
        val canReuse = HomeCurationQueryPolicy.canReuse(initial, snapshot, requestedRevision = 1L, tabId = "CURATION")

        // Then
        assertFalse(canReuse)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_같은조회조건의_섹션요청이_진행중이면_중복요청을_하지_않는다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val snapshot = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 2L)
        val loading = HomeCurationStateReducer.startSectionLoad(loaded, "CURATION", snapshot.location, requestId = 2L)
            .copy(section = null)

        // When
        val canReuse = HomeCurationQueryPolicy.canReuse(loading, snapshot, requestedRevision = 2L, tabId = "CURATION")

        // Then
        assertTrue(canReuse)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_섹션조회가_실패했으면_같은조회조건도_다시_요청한다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val snapshot = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 2L)
        val loading = HomeCurationStateReducer.startSectionLoad(loaded, "CURATION", snapshot.location, requestId = 2L)
        val failed = HomeCurationStateReducer.failSection(loading, "조회 실패", requestId = 2L)

        // When
        val canReuse = HomeCurationQueryPolicy.canReuse(failed, snapshot, requestedRevision = 2L, tabId = "CURATION")

        // Then
        assertFalse(canReuse)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_조회세대나_좌표나_탭이_다르면_기존캐시를_재사용하지_않는다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val snapshot = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 4L)

        // When
        val oldRevision = HomeCurationQueryPolicy.canReuse(loaded, snapshot, requestedRevision = 3L, tabId = "CURATION")
        val noRevision = HomeCurationQueryPolicy.canReuse(loaded, snapshot, requestedRevision = null, tabId = "CURATION")
        val differentMapLocation = HomeCurationQueryPolicy.canReuse(
            loaded, snapshot.copy(location = snapshot.location.copy(mapLatitude = 37.57)),
            requestedRevision = 4L, tabId = "CURATION",
        )
        val differentDeviceLocation = HomeCurationQueryPolicy.canReuse(
            loaded, snapshot.copy(location = snapshot.location.copy(deviceLatitude = null, deviceLongitude = null)),
            requestedRevision = 4L, tabId = "CURATION",
        )
        val differentTab = HomeCurationQueryPolicy.canReuse(loaded, snapshot, requestedRevision = 4L, tabId = "FUTURE_TAB")

        // Then
        assertFalse(oldRevision)
        assertFalse(noRevision)
        assertFalse(differentMapLocation)
        assertFalse(differentDeviceLocation)
        assertFalse(differentTab)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_섹션요청중에도_세대나_좌표나_탭이_다르면_재사용하지_않는다`() {
        // Given
        val loaded = HomeCurationTestFixtures.loadedState()
        val snapshot = HomeCurationQuerySnapshot(HomeCurationTestFixtures.location, 4L)
        val loading = HomeCurationStateReducer.startSectionLoad(loaded, "CURATION", snapshot.location, requestId = 4L)

        // When
        val differentRevision = HomeCurationQueryPolicy.canReuse(loading, snapshot.copy(revision = 5L), requestedRevision = 4L, tabId = "CURATION")
        val differentLocation = HomeCurationQueryPolicy.canReuse(
            loading, snapshot.copy(location = snapshot.location.copy(mapLongitude = 126.96)),
            requestedRevision = 4L, tabId = "CURATION",
        )
        val differentTab = HomeCurationQueryPolicy.canReuse(loading, snapshot, requestedRevision = 4L, tabId = "FUTURE_TAB")

        // Then
        assertFalse(differentRevision)
        assertFalse(differentLocation)
        assertFalse(differentTab)
    }
}
