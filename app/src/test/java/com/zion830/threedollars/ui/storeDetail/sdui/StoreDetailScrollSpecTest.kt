package com.zion830.threedollars.ui.storeDetail.sdui

import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailScrollSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailScrollSpecTest {

    // TH-1226 TC7
    @Test
    fun `TH1226_TC7_가게명이_네비아래로_지나가는_0에서48dp구간에서_제목이_서서히_나타난다`() {
        // Given
        val previewIndex = 0

        // When
        val atTop = StoreDetailScrollSpec.titleAlpha(firstVisibleIndex = 0, firstVisibleOffsetDp = 0f, previewIndex = previewIndex)
        val half = StoreDetailScrollSpec.titleAlpha(firstVisibleIndex = 0, firstVisibleOffsetDp = 24f, previewIndex = previewIndex)
        val passed = StoreDetailScrollSpec.titleAlpha(firstVisibleIndex = 0, firstVisibleOffsetDp = 80f, previewIndex = previewIndex)
        val nextItem = StoreDetailScrollSpec.titleAlpha(firstVisibleIndex = 3, firstVisibleOffsetDp = 0f, previewIndex = previewIndex)

        // Then
        assertEquals(0f, atTop, 0f)
        assertEquals(0.5f, half, 0.001f)
        assertEquals(1f, passed, 0f)
        assertEquals(1f, nextItem, 0f)
    }

    // TH-1226 TC7
    @Test
    fun `TH1226_TC7_콜아웃이_미리보기위에_있으면_미리보기에_닿기전까지_제목은_숨는다`() {
        // Given
        val previewIndex = 1

        // When
        val alpha = StoreDetailScrollSpec.titleAlpha(firstVisibleIndex = 0, firstVisibleOffsetDp = 40f, previewIndex = previewIndex)

        // Then
        assertEquals(0f, alpha, 0f)
    }

    // TH-1226 TC8
    @Test
    fun `TH1226_TC8_고정탭아래에_걸린_섹션에_해당하는_탭이_선택된다`() {
        // Given
        val tabTargets = listOf(0, 4, 6, 8)

        // When
        val onInfo = StoreDetailScrollSpec.selectedTab(tabTargets, anchorIndex = 5, isAtBottom = false)
        val onImage = StoreDetailScrollSpec.selectedTab(tabTargets, anchorIndex = 6, isAtBottom = false)
        val beforeInfo = StoreDetailScrollSpec.selectedTab(tabTargets, anchorIndex = 3, isAtBottom = false)

        // Then
        assertEquals(1, onInfo)
        assertEquals(2, onImage)
        assertEquals(0, beforeInfo)
    }

    // TH-1226 TC8
    @Test
    fun `TH1226_TC8_목록끝에_닿으면_마지막탭을_선택한다`() {
        // Given
        val tabTargets = listOf(0, 4, 6, null)

        // When
        val selected = StoreDetailScrollSpec.selectedTab(tabTargets, anchorIndex = 4, isAtBottom = true)

        // Then
        assertEquals(2, selected)
    }

    // TH-1226 TC10
    @Test
    fun `TH1226_TC10_액션버튼줄이_목록위로_사라지면_하단칩바를_보이고_다시보이면_숨긴다`() {
        // Given
        val viewportTop = 200f

        // When
        val hidden = StoreDetailScrollSpec.isBottomBarVisible(true, previewIndex = 0, firstVisibleIndex = 0, actionBarBottom = 320f, viewportTop = viewportTop)
        val scrolledAway = StoreDetailScrollSpec.isBottomBarVisible(true, previewIndex = 0, firstVisibleIndex = 0, actionBarBottom = 180f, viewportTop = viewportTop)
        val previewGone = StoreDetailScrollSpec.isBottomBarVisible(true, previewIndex = 0, firstVisibleIndex = 4, actionBarBottom = null, viewportTop = viewportTop)
        val noActions = StoreDetailScrollSpec.isBottomBarVisible(false, previewIndex = 0, firstVisibleIndex = 4, actionBarBottom = null, viewportTop = viewportTop)

        // Then
        assertFalse(hidden)
        assertTrue(scrolledAway)
        assertTrue(previewGone)
        assertFalse(noActions)
    }
}
