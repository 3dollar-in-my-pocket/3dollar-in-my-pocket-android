package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeStoreLandingRequestTest {

    // TH-1537 TC1
    @Test
    fun `TH1537_TC1_앱밖에서온_store링크면_그가게로_홈랜딩을_요청한다`() {
        // Given
        val storeId = "12806182"

        // When
        val request = HomeStoreLandingRequest.from(storeId = storeId, view = null, isExternal = true)

        // Then
        assertEquals(HomeStoreLandingRequest(storeId = 12806182L, expanded = true), request)
    }

    // TH-1537 TC3
    @Test
    fun `TH1537_TC3_view가_collapsed면_미리보기로_연다`() {
        // Given
        val view = "collapsed"

        // When
        val request = HomeStoreLandingRequest.from(storeId = "100", view = view, isExternal = true)

        // Then
        assertEquals(false, request?.expanded)
    }

    // TH-1537 TC4
    @Test
    fun `TH1537_TC4_view가_expanded면_펼쳐서_연다`() {
        // Given
        val view = "expanded"

        // When
        val request = HomeStoreLandingRequest.from(storeId = "100", view = view, isExternal = true)

        // Then
        assertEquals(true, request?.expanded)
    }

    // TH-1537 TC5
    @Test
    fun `TH1537_TC5_view가_없거나_알수없는값이면_펼쳐서_연다`() {
        // Given
        val views = listOf(null, "", "unknown", "COLLAPSED")

        // When
        val expanded = views.map { HomeStoreLandingRequest.from(storeId = "100", view = it, isExternal = true)?.expanded }

        // Then
        assertEquals(listOf(true, true, true, true), expanded)
    }

    // TH-1537 TC10
    @Test
    fun `TH1537_TC10_앱안에서연_store링크면_홈랜딩하지않고_기존상세로간다`() {
        // Given
        val isExternal = false

        // When
        val request = HomeStoreLandingRequest.from(storeId = "100", view = "collapsed", isExternal = isExternal)

        // Then
        assertNull(request)
    }

    @Test
    fun `회귀_storeId가_숫자가아니거나_0이하면_홈랜딩하지않는다`() {
        // Given
        val storeIds = listOf(null, "", "abc", "0", "-1")

        // When
        val requests = storeIds.map { HomeStoreLandingRequest.from(storeId = it, view = null, isExternal = true) }

        // Then
        assertEquals(listOf(null, null, null, null, null), requests)
    }
}
