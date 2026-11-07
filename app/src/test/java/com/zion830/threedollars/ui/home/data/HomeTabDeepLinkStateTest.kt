package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeTabDeepLinkStateTest {

    // TH-1579 TC2
    @Test
    fun `TH1579_TC2_tabId응답의_defaultSelected탭을_선택한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs("HomeFilterScreenTabIdNews.json")

        // When
        val state = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs, resetSelection = true)

        // Then
        assertEquals(listOf("DEFAULT", "CURATION", "NEWS"), state.tabs.map { it.tabId })
        assertEquals("NEWS", state.selectedTabId)
    }

    // TH-1579 TC4
    @Test
    fun `TH1579_TC4_tabId없이_다시받으면_사용자가_고른탭을_유지한다`() {
        // Given
        val tabs = HomeCurationTestFixtures.tabs("HomeFilterScreenWithNewsTab.json")
        val selected = HomeCurationStateReducer.selectTab(
            HomeCurationStateReducer.updateTabs(HomeCurationUiState(), tabs),
            "CURATION",
        )

        // When
        val state = HomeCurationStateReducer.updateTabs(selected, tabs)

        // Then
        assertEquals("CURATION", state.selectedTabId)
    }

    // TH-1579 TC5
    @Test
    fun `TH1579_TC5_앱실행중_다른탭을_보고있어도_딥링크응답의_defaultSelected로_바꾼다`() {
        // Given
        val defaultTabs = HomeCurationTestFixtures.tabs("HomeFilterScreenWithNewsTab.json")
        val viewing = HomeCurationStateReducer.selectTab(
            HomeCurationStateReducer.updateTabs(HomeCurationUiState(), defaultTabs),
            "CURATION",
        )
        val deepLinkTabs = HomeCurationTestFixtures.tabs("HomeFilterScreenTabIdNews.json")

        // When
        val state = HomeCurationStateReducer.updateTabs(viewing, deepLinkTabs, resetSelection = true)

        // Then
        assertEquals("NEWS", state.selectedTabId)
    }

    // TH-1579 TC5
    @Test
    fun `TH1579_TC5_콜드스타트에서_딥링크응답뒤_기본응답이_와도_딥링크탭을_유지한다`() {
        // Given
        val deepLinkTabs = HomeCurationTestFixtures.tabs("HomeFilterScreenTabIdNews.json")
        val defaultTabs = HomeCurationTestFixtures.tabs("HomeFilterScreenWithNewsTab.json")
        val landed = HomeCurationStateReducer.updateTabs(HomeCurationUiState(), deepLinkTabs, resetSelection = true)

        // When
        val state = HomeCurationStateReducer.updateTabs(landed, defaultTabs)

        // Then
        assertEquals("NEWS", state.selectedTabId)
    }
}
