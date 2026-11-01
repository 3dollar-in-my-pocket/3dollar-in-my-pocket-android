package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeNewsTabStateTest {

    // TH-1427 TC1
    @Test
    fun `TH1427_TC1_NEWS포함_3탭을_서버순서로_두고_서버기본탭을_선택한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs("HomeFilterScreenWithNewsTab.json")

        // When
        val state = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs)

        // Then
        assertEquals(listOf("DEFAULT", "CURATION", "NEWS"), state.tabs.map { it.tabId })
        assertEquals("DEFAULT", state.selectedTabId)
        assertTrue(state.isStoreListVisible)
        assertFalse(state.isNewsTabSelected)
    }

    // TH-1427 TC2
    @Test
    fun `TH1427_TC2_동네소식탭을_누르면_소식탭이_선택되고_서버_클릭로그를_가진다`() {
        // Given
        val state = HomeCurationStateReducer.updateTabs(
            HomeCurationUiState(),
            HomeCurationTestFixtures.tabs("HomeFilterScreenWithNewsTab.json"),
        )
        val newsTab = state.tabs.single { it.tabId == "NEWS" }

        // When
        val selected = HomeCurationStateReducer.selectTab(state, newsTab.tabId)

        // Then
        assertTrue(selected.isNewsTabSelected)
        assertFalse(selected.isCurationTabSelected)
        assertFalse(selected.isStoreListVisible)
        assertEquals("NEWS", newsTab.clickLog?.extraParameters?.get("value"))
    }

    // TH-1427 TC14
    @Test
    fun `TH1427_TC14_탭을_바꿀때만_바뀐탭_목록을_맨위로_올린다`() {
        // Given
        val previous = "DEFAULT"

        // When
        val changed = HomeSheetTabScrollPolicy.shouldScrollToTop(previous, "NEWS")
        val same = HomeSheetTabScrollPolicy.shouldScrollToTop("NEWS", "NEWS")
        val firstSelection = HomeSheetTabScrollPolicy.shouldScrollToTop(null, "DEFAULT")

        // Then
        assertTrue(changed)
        assertFalse(same)
        assertFalse(firstSelection)
    }

    // TH-1427 TC15
    @Test
    fun `TH1427_TC15_앱을_새로열면_이전에_보던탭과_무관하게_서버기본탭으로_시작한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs("HomeFilterScreenWithNewsTab.json")
        val previousRun = HomeCurationStateReducer.selectTab(HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs), "NEWS")

        // When
        val relaunched = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs)
        val sameRun = HomeCurationStateReducer.updateTabs(previousRun, tabs)

        // Then
        assertEquals("DEFAULT", relaunched.selectedTabId)
        assertEquals("NEWS", sameRun.selectedTabId)
    }

    // TH-1427 TC19
    @Test
    fun `TH1427_TC19_모르는viewType탭은_숨기고_NEWS탭은_남긴다`() {
        // Given
        val tabs = HomeCurationTestFixtures.rawTabs("HomeFilterScreenWithNewsAndUnknownTab.json")

        // When
        val state = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs)

        // Then
        assertEquals(listOf("DEFAULT", "NEWS"), state.tabs.map { it.tabId })
        assertEquals("DEFAULT", state.selectedTabId)
    }
}
