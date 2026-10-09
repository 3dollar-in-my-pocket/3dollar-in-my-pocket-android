package com.zion830.threedollars.ui.home.ui

internal object HomeMapControlsVisibilityPolicy {
    fun isVisible(
        sheetTopPx: Int,
        headerBottomPx: Int,
        controlsHeightPx: Int,
        gapFromSheetPx: Int,
        isStorePreviewShowing: Boolean,
    ): Boolean {
        if (isStorePreviewShowing || controlsHeightPx <= 0 || sheetTopPx <= headerBottomPx) return false
        return sheetTopPx - gapFromSheetPx - controlsHeightPx >= headerBottomPx
    }
}
