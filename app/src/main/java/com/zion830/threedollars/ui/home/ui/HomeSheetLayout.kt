package com.zion830.threedollars.ui.home.ui

internal object HomeSheetLayout {
    // Figma shows 349dp from screen bottom; the home fragment sits above the 56dp bottom navigation.
    const val COLLAPSED_PEEK_HEIGHT_DP = 293f
    const val LOCATION_BUTTON_GAP_FROM_SHEET_DP = 16f

    /** 지도 컨트롤·제보 버튼 그림자가 ComposeView 경계에서 잘리지 않도록 안쪽에 두는 여백. 바깥 여백에서 그만큼 뺀다. */
    const val MAP_CONTROL_SHADOW_INSET_DP = 4f

    const val LOCATION_BUTTON_BOTTOM_MARGIN_DP =
        COLLAPSED_PEEK_HEIGHT_DP + LOCATION_BUTTON_GAP_FROM_SHEET_DP
}
