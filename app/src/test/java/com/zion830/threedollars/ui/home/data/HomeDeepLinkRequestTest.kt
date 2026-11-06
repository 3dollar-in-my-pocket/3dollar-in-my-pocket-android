package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
}
