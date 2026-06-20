package com.zion830.threedollars.ui.home.ui

internal object HomeSheetLayout {
    // Figma shows 349dp from screen bottom; the home fragment sits above the 56dp bottom navigation.
    const val COLLAPSED_PEEK_HEIGHT_DP = 293f
    const val LOCATION_BUTTON_GAP_FROM_SHEET_DP = 16f

    const val LOCATION_BUTTON_BOTTOM_MARGIN_DP =
        COLLAPSED_PEEK_HEIGHT_DP + LOCATION_BUTTON_GAP_FROM_SHEET_DP
}
