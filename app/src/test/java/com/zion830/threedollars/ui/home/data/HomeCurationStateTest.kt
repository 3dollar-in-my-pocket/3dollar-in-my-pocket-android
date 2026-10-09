package com.zion830.threedollars.ui.home.data

import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeCurationStateTest {

    // TH-1401 TC1
    @Test
    fun `TH1401_TC1_서버기본탭을_선택하고_지원탭_순서를_유지한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs()

        // When
        val state = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs)

        // Then
        assertEquals(listOf("CURATION", "DEFAULT"), state.tabs.map { it.tabId })
        assertEquals(tabs.tabs, state.tabs)
        assertEquals("CURATION", state.selectedTabId)
    }

    // TH-1401 TC1
    @Test
    fun `TH1401_TC1_이미선택한탭이_새응답에도_있으면_선택을_보존한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs()
        val selected = HomeCurationStateReducer.selectTab(
            HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs),
            "DEFAULT",
        )

        // When
        val state = HomeCurationStateReducer.updateTabs(selected, tabs)

        // Then
        assertEquals("DEFAULT", state.selectedTabId)
    }

    // TH-1401 TC1
    @Test
    fun `TH1401_TC1_기본선택이_없으면_첫지원탭을_선택한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs("HomeFilterScreenWithoutDefaultTab.json")

        // When
        val state = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs)

        // Then
        assertEquals("CURATION", state.selectedTabId)
    }

    // TH-1401 TC1
    @Test
    fun `TH1401_TC1_두번째탭이_서버기본이면_순서와_무관하게_선택한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs("HomeFilterScreenWithSecondDefaultTab.json")

        // When
        val state = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs)

        // Then
        assertEquals(listOf("CURATION", "DEFAULT"), state.tabs.map { it.tabId })
        assertEquals("DEFAULT", state.selectedTabId)
    }

    // TH-1401 TC2, TC3
    @Test
    fun `TH1401_TC2_주변목록탭으로_전환해도_큐레이션카드와_카테고리를_보존한다`() {
        // Given
        val loaded = loadedState()

        // When
        val state = HomeCurationStateReducer.selectTab(loaded, "DEFAULT")

        // Then
        assertEquals("DEFAULT", state.selectedTabId)
        assertSame(loaded.section, state.section)
        assertSame(loaded.carousels, state.carousels)
    }

    // TH-1401 TC3
    @Test
    fun `TH1401_TC3_큐레이션탭으로_돌아오면_이전카테고리와_카드를_보존한다`() {
        // Given
        val loaded = loadedState()
        val loading = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)
        val changed = HomeCurationStateReducer.applyCategory(
            loading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, HomeCurationTestFixtures.cards(),
        )
        val storeList = HomeCurationStateReducer.selectTab(changed, "DEFAULT")

        // When
        val state = HomeCurationStateReducer.selectTab(storeList, "CURATION")

        // Then
        assertEquals("CURATION", state.selectedTabId)
        assertEquals("WAKBBU_SALT_BREAD", state.carousels.getValue("POPULAR_SNACKS").selectedCategoryId)
        assertSame(changed.carousels, state.carousels)
    }

    // TH-1401 TC4
    @Test
    fun `TH1401_TC4_섹션응답의_초기카드와_기본카테고리를_바로_사용한다`() {
        // Given
        val section = HomeCurationTestFixtures.section()
        val loading = HomeCurationStateReducer.startSectionLoad(
            HomeCurationStateReducer.updateTabs(HomeCurationUiState(), HomeCurationTestFixtures.tabs()),
            "CURATION", HomeCurationTestFixtures.location, 1L,
        )

        // When
        val state = HomeCurationStateReducer.applySection(loading, section, 1L)

        // Then
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals("CURATION", state.sectionTabId)
        assertEquals(HomeCurationTestFixtures.location, state.requestLocation)
        assertEquals(section, state.section)
        assertEquals(setOf("POPULAR_SNACKS", "TASTE_SNACKS"), state.carousels.keys)
        section.items.filterIsInstance<SDHomeCurationItemModel.Carousel>().forEach { carousel ->
            val carouselState = state.carousels.getValue(carousel.carouselId)
            assertEquals(carousel.defaultCategoryId, carouselState.selectedCategoryId)
            assertEquals(carousel.cards, carouselState.cards.cards)
            assertFalse(carouselState.isLoading)
            assertNull(carouselState.pendingCategoryId)
            assertEquals(0L, carouselState.requestId)
        }
    }

    // TH-1401 TC4
    @Test
    fun `TH1401_TC4_지도재조회_성공시_서버기본카테고리와_초기카드로_교체한다`() {
        // Given
        val loaded = loadedState()
        val categoryLoading = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)
        val changed = HomeCurationStateReducer.applyCategory(
            categoryLoading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, HomeCurationTestFixtures.cards(),
        )
        val location = HomeCurationTestFixtures.location.copy(mapLatitude = 37.56, mapLongitude = 126.97)
        val loading = HomeCurationStateReducer.startSectionLoad(changed, "CURATION", location, 3L)
        val section = HomeCurationTestFixtures.section()

        // When
        val state = HomeCurationStateReducer.applySection(loading, section, 3L)

        // Then
        assertEquals("BUNGEOPPANG", state.carousels.getValue("POPULAR_SNACKS").selectedCategoryId)
        assertEquals(HomeCurationTestFixtures.carousel(section, "POPULAR_SNACKS").cards, state.carousels.getValue("POPULAR_SNACKS").cards.cards)
        assertEquals(location, state.requestLocation)
        assertEquals(0L, state.carousels.getValue("POPULAR_SNACKS").requestId)
    }

    // TH-1401 TC4
    @Test
    fun `TH1401_TC4_섹션조회_실패시_이전카드와_선택을_유지하고_오류를_표시한다`() {
        // Given
        val loaded = loadedState()
        val loading = HomeCurationStateReducer.startSectionLoad(loaded, "CURATION", HomeCurationTestFixtures.location, 2L)

        // When
        val state = HomeCurationStateReducer.failSection(loading, "조회 실패", 2L)

        // Then
        assertFalse(state.isLoading)
        assertEquals("조회 실패", state.errorMessage)
        assertSame(loaded.section, state.section)
        assertEquals(loaded.carousels, state.carousels)
    }

    // TH-1401 TC4
    @Test
    fun `TH1401_TC4_늦게도착한_이전섹션응답과_실패를_무시한다`() {
        // Given
        val loaded = loadedState()
        val loading = HomeCurationStateReducer.startSectionLoad(loaded, "CURATION", HomeCurationTestFixtures.location, 3L)

        // When
        val success = HomeCurationStateReducer.applySection(loading, HomeCurationTestFixtures.section(), 2L)
        val failure = HomeCurationStateReducer.failSection(loading, "늦은 실패", 2L)

        // Then
        assertSame(loading, success)
        assertSame(loading, failure)
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_카테고리조회중에는_이전선택과_카드를_유지하고_대상만_로딩한다`() {
        // Given
        val loaded = loadedState()
        val previous = loaded.carousels.getValue("POPULAR_SNACKS")

        // When
        val state = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)

        // Then
        val carousel = state.carousels.getValue("POPULAR_SNACKS")
        assertEquals("BUNGEOPPANG", carousel.selectedCategoryId)
        assertSame(previous.cards, carousel.cards)
        assertTrue(carousel.isLoading)
        assertNull(carousel.errorMessage)
        assertEquals("WAKBBU_SALT_BREAD", carousel.pendingCategoryId)
        assertEquals(2L, carousel.requestId)
        assertSame(loaded.carousels.getValue("TASTE_SNACKS"), state.carousels.getValue("TASTE_SNACKS"))
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_카테고리응답이_성공하면_해당캐러셀의_선택과_카드만_동시에_교체한다`() {
        // Given
        val loaded = loadedState()
        val cards = HomeCurationTestFixtures.cards()
        val loading = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)
        assertNotEquals(loaded.carousels.getValue("POPULAR_SNACKS").cards.cards, cards.cards)

        // When
        val state = HomeCurationStateReducer.applyCategory(loading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, cards)

        // Then
        val carousel = state.carousels.getValue("POPULAR_SNACKS")
        assertEquals("WAKBBU_SALT_BREAD", carousel.selectedCategoryId)
        assertSame(cards, carousel.cards)
        assertFalse(carousel.isLoading)
        assertNull(carousel.pendingCategoryId)
        assertNull(carousel.errorMessage)
        assertSame(loaded.carousels.getValue("TASTE_SNACKS"), state.carousels.getValue("TASTE_SNACKS"))
        assertSame(loaded.section, state.section)
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_카테고리실패시_이전선택과_카드를_유지하고_재시도대상을_보존한다`() {
        // Given
        val loaded = loadedState()
        val loading = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)

        // When
        val state = HomeCurationStateReducer.failCategory(loading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, "카드 조회 실패")

        // Then
        val carousel = state.carousels.getValue("POPULAR_SNACKS")
        assertEquals("BUNGEOPPANG", carousel.selectedCategoryId)
        assertSame(loaded.carousels.getValue("POPULAR_SNACKS").cards, carousel.cards)
        assertFalse(carousel.isLoading)
        assertEquals("카드 조회 실패", carousel.errorMessage)
        assertEquals("WAKBBU_SALT_BREAD", carousel.pendingCategoryId)
        assertSame(loaded.carousels.getValue("TASTE_SNACKS"), state.carousels.getValue("TASTE_SNACKS"))
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_실패한카테고리를_재시도하면_오류를_비우고_성공응답을_반영한다`() {
        // Given
        val loaded = loadedState()
        val loading = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)
        val failed = HomeCurationStateReducer.failCategory(loading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, "조회 실패")
        val cards = HomeCurationTestFixtures.cards()

        // When
        val retrying = HomeCurationStateReducer.startCategoryLoad(failed, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 3L)
        val state = HomeCurationStateReducer.applyCategory(retrying, "POPULAR_SNACKS", 3L, loaded.sectionRequestId, cards)

        // Then
        val pending = retrying.carousels.getValue("POPULAR_SNACKS")
        assertTrue(pending.isLoading)
        assertNull(pending.errorMessage)
        assertEquals("BUNGEOPPANG", pending.selectedCategoryId)
        assertSame(loaded.carousels.getValue("POPULAR_SNACKS").cards, pending.cards)
        val complete = state.carousels.getValue("POPULAR_SNACKS")
        assertEquals("WAKBBU_SALT_BREAD", complete.selectedCategoryId)
        assertSame(cards, complete.cards)
        assertFalse(complete.isLoading)
        assertNull(complete.pendingCategoryId)
        assertNull(complete.errorMessage)
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_두캐러셀을_동시에_조회해도_각응답과_로딩상태를_독립적으로_반영한다`() {
        // Given
        val loaded = loadedState()
        val popularLoading = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)
        val bothLoading = HomeCurationStateReducer.startCategoryLoad(popularLoading, "TASTE_SNACKS", "SWEET_TASTE", 3L)
        val cards = HomeCurationTestFixtures.cards()

        // When
        val state = HomeCurationStateReducer.applyCategory(bothLoading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, cards)

        // Then
        assertEquals("WAKBBU_SALT_BREAD", state.carousels.getValue("POPULAR_SNACKS").selectedCategoryId)
        assertSame(cards, state.carousels.getValue("POPULAR_SNACKS").cards)
        assertFalse(state.carousels.getValue("POPULAR_SNACKS").isLoading)
        assertSame(bothLoading.carousels.getValue("TASTE_SNACKS"), state.carousels.getValue("TASTE_SNACKS"))
        assertTrue(state.carousels.getValue("TASTE_SNACKS").isLoading)
        assertEquals("SWEET_TASTE", state.carousels.getValue("TASTE_SNACKS").pendingCategoryId)
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_같은캐러셀의_늦은성공과_실패가_최신선택을_덮지_않는다`() {
        // Given
        val loaded = loadedState()
        val first = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)
        val latest = HomeCurationStateReducer.startCategoryLoad(first, "POPULAR_SNACKS", "FRUIT_SANDO", 3L)

        // When
        val success = HomeCurationStateReducer.applyCategory(latest, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, HomeCurationTestFixtures.cards())
        val failure = HomeCurationStateReducer.failCategory(latest, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, "늦은 실패")

        // Then
        assertSame(latest, success)
        assertSame(latest, failure)
        assertEquals("FRUIT_SANDO", latest.carousels.getValue("POPULAR_SNACKS").pendingCategoryId)
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_새섹션을_조회한뒤_이전카테고리응답을_무시한다`() {
        // Given
        val loaded = loadedState()
        val categoryLoading = HomeCurationStateReducer.startCategoryLoad(loaded, "POPULAR_SNACKS", "WAKBBU_SALT_BREAD", 2L)
        val sectionLoading = HomeCurationStateReducer.startSectionLoad(categoryLoading, "CURATION", HomeCurationTestFixtures.location, 3L)

        // When
        val success = HomeCurationStateReducer.applyCategory(sectionLoading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, HomeCurationTestFixtures.cards())
        val failure = HomeCurationStateReducer.failCategory(sectionLoading, "POPULAR_SNACKS", 2L, loaded.sectionRequestId, "이전 지도 실패")

        // Then
        assertSame(sectionLoading, success)
        assertSame(sectionLoading, failure)
        assertFalse(sectionLoading.carousels.getValue("POPULAR_SNACKS").isLoading)
        assertNull(sectionLoading.carousels.getValue("POPULAR_SNACKS").pendingCategoryId)
    }

    // TH-1401 TC8
    @Test
    fun `TH1401_TC8_지원하지_않는탭을_제외하고_현재선택이_사라지면_기본탭을_선택한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.rawTabs("HomeFilterScreenWithUnsupportedTab.json")
        val current = HomeCurationUiState(selectedTabId = "DEFAULT")

        // When
        val state = HomeCurationStateReducer.updateTabs(current, tabs)

        // Then
        assertEquals(listOf("CURATION"), state.tabs.map { it.tabId })
        assertEquals("CURATION", state.selectedTabId)
    }

    // TH-1401 TC8
    @Test
    fun `TH1401_TC8_없는탭을_선택해도_현재상태를_유지한다`() {
        // Given
        val loaded = loadedState()

        // When
        val state = HomeCurationStateReducer.selectTab(loaded, "FUTURE_TAB")

        // Then
        assertSame(loaded, state)
    }

    // TH-1401 TC8
    @Test
    fun `TH1401_TC8_지원탭이_없으면_선택을_비운다`() {
        // Given
        val loaded = loadedState()

        // When
        val state = HomeCurationStateReducer.updateTabs(loaded, HomeCurationTestFixtures.tabs("HomeFilterScreenWithoutTabs.json"))

        // Then
        assertTrue(state.tabs.isEmpty())
        assertNull(state.selectedTabId)
    }

    private fun loadedState(): HomeCurationUiState = HomeCurationTestFixtures.loadedState()
}
