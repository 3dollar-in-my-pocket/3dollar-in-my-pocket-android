package com.zion830.threedollars.ui.home.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeMapControlsVisibilityPolicyTest {

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_지도공간이_충분하면_컨트롤을_표시한다`() {
        // Given
        val sheetTopPx = 700
        val headerBottomPx = 200

        // When
        val visible = HomeMapControlsVisibilityPolicy.isVisible(
            sheetTopPx = sheetTopPx,
            headerBottomPx = headerBottomPx,
            controlsHeightPx = 100,
            gapFromSheetPx = 12,
            isStorePreviewShowing = false,
        )

        // Then
        assertTrue(visible)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_접힌시트를_전체로_올리면_컨트롤을_숨긴다`() {
        // Given
        val headerBottomPx = 200

        // When
        val collapsed = HomeMapControlsVisibilityPolicy.isVisible(700, headerBottomPx, 100, 12, false)
        val full = HomeMapControlsVisibilityPolicy.isVisible(200, headerBottomPx, 100, 12, false)

        // Then
        assertTrue(collapsed)
        assertFalse(full)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_드래그중_컨트롤이_헤더경계에_닿으면_표시하고_침범하면_숨긴다`() {
        // Given
        val headerBottomPx = 200
        val controlsHeightPx = 100
        val gapFromSheetPx = 12

        // When
        val atBoundary = HomeMapControlsVisibilityPolicy.isVisible(312, headerBottomPx, controlsHeightPx, gapFromSheetPx, false)
        val overlapsHeader = HomeMapControlsVisibilityPolicy.isVisible(311, headerBottomPx, controlsHeightPx, gapFromSheetPx, false)
        val largerPixelGap = HomeMapControlsVisibilityPolicy.isVisible(312, headerBottomPx, controlsHeightPx, 24, false)

        // Then
        assertTrue(atBoundary)
        assertFalse(overlapsHeader)
        assertFalse(largerPixelGap)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_가게미리보기중에는_숨기고_닫으면_지도공간에_맞춰_복원한다`() {
        // Given
        val sheetTopPx = 700
        val headerBottomPx = 200

        // When
        val preview = HomeMapControlsVisibilityPolicy.isVisible(sheetTopPx, headerBottomPx, 100, 12, true)
        val closed = HomeMapControlsVisibilityPolicy.isVisible(sheetTopPx, headerBottomPx, 100, 12, false)

        // Then
        assertFalse(preview)
        assertTrue(closed)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_같은지도공간에서_키큰컨트롤이_헤더를_침범하면_숨긴다`() {
        // Given
        val sheetTopPx = 500
        val headerBottomPx = 250

        // When
        val shortControls = HomeMapControlsVisibilityPolicy.isVisible(sheetTopPx, headerBottomPx, 100, 12, false)
        val tallControls = HomeMapControlsVisibilityPolicy.isVisible(sheetTopPx, headerBottomPx, 260, 12, false)

        // Then
        assertTrue(shortControls)
        assertFalse(tallControls)
    }

    // TH-1401 TC12
    @Test
    fun `TH1401_TC12_컨트롤높이가_아직_측정되지_않았으면_숨긴다`() {
        // Given
        val sheetTopPx = 700
        val headerBottomPx = 200

        // When
        val unmeasured = HomeMapControlsVisibilityPolicy.isVisible(sheetTopPx, headerBottomPx, 0, 12, false)
        val invalidHeight = HomeMapControlsVisibilityPolicy.isVisible(sheetTopPx, headerBottomPx, -1, 12, false)

        // Then
        assertFalse(unmeasured)
        assertFalse(invalidHeight)
    }
}
