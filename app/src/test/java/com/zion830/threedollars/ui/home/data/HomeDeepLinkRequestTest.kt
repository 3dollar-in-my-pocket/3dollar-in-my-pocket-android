package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeDeepLinkRequestTest {

    // TH-1579 TC1
    @Test
    fun `TH1579_TC1_tabId만_있으면_탭만_다시받도록_값을_그대로_넘긴다`() {
        // Given
        val tabId = "NEWS"

        // When
        val request = HomeDeepLinkRequest.from(preset = null, tabId = tabId)

        // Then
        assertEquals(HomeDeepLinkRequest.Tabs(tabId = "NEWS"), request)
    }

    // TH-1579 TC1
    @Test
    fun `TH1579_TC1_tabId는_대소문자와_공백을_바꾸지_않고_넘긴다`() {
        // Given
        val tabIds = listOf("news", " NEWS ", "")

        // When
        val requests = tabIds.map { HomeDeepLinkRequest.from(preset = null, tabId = it) }

        // Then
        assertEquals(tabIds.map { HomeDeepLinkRequest.Tabs(tabId = it) }, requests)
    }

    // TH-1579 TC3
    @Test
    fun `TH1579_TC3_preset과_tabId가_함께오면_둘다_넘긴다`() {
        // Given
        val preset = "SNACK"
        val tabId = "CURATION"

        // When
        val request = HomeDeepLinkRequest.from(preset = preset, tabId = tabId)

        // Then
        assertEquals(HomeDeepLinkRequest.Screen(preset = "SNACK", tabId = "CURATION"), request)
    }

    // TH-1579 TC4
    @Test
    fun `TH1579_TC4_파라미터가_없으면_홈화면을_다시받지_않는다`() {
        // Given
        val preset: String? = null
        val tabId: String? = null

        // When
        val request = HomeDeepLinkRequest.from(preset = preset, tabId = tabId)

        // Then
        assertNull(request)
    }

    // TH-1579 TC4
    @Test
    fun `TH1579_TC4_preset만_있으면_기존처럼_tabId없이_홈화면을_받는다`() {
        // Given
        val preset = "SNACK"

        // When
        val request = HomeDeepLinkRequest.from(preset = preset, tabId = null)

        // Then
        assertEquals(HomeDeepLinkRequest.Screen(preset = "SNACK", tabId = null), request)
    }

    // TH-1579 TC4
    @Test
    fun `TH1579_TC4_tabId없이_받은_응답은_고른탭을_초기화하지_않는다`() {
        // Given
        val requestedTabId: String? = null

        // When
        val shouldReset = HomeDeepLinkRequest.shouldResetTabSelection(requestedTabId, requestSerial = 1L, latestSerial = 1L)

        // Then
        assertFalse(shouldReset)
    }

    // TH-1579 TC5
    @Test
    fun `TH1579_TC5_가장최근_딥링크의_tabId응답이면_고른탭을_초기화한다`() {
        // Given
        val requestedTabId = "NEWS"

        // When
        val shouldReset = HomeDeepLinkRequest.shouldResetTabSelection(requestedTabId, requestSerial = 2L, latestSerial = 2L)

        // Then
        assertTrue(shouldReset)
    }

    // TH-1579 TC5
    @Test
    fun `TH1579_TC5_뒤이어_다른딥링크가_오면_앞선응답은_고른탭을_초기화하지_않는다`() {
        // Given
        val requestedTabId = "CURATION"

        // When
        val shouldReset = HomeDeepLinkRequest.shouldResetTabSelection(requestedTabId, requestSerial = 1L, latestSerial = 2L)

        // Then
        assertFalse(shouldReset)
    }
}
