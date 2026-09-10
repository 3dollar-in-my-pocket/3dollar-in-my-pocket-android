package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.model.SDLocationModel

internal data class HomeInitialCameraCommand(
    val target: SDLocationModel,
    val zoom: Double?,
)

internal class HomeInitialCameraPolicy {
    private var mapReady = false
    private var filterResolved = false
    private var initialTarget: SDLocationModel? = null
    private var configuredZoom: Double? = null
    private var restored = false
    private var consumed = false

    fun onInitialTarget(target: SDLocationModel, restored: Boolean): HomeInitialCameraCommand? {
        if (consumed) return null
        initialTarget = target
        this.restored = restored
        return commandIfReady()
    }

    fun onMapReady(): HomeInitialCameraCommand? {
        mapReady = true
        return commandIfReady()
    }

    fun onFilterResolved(initialMapZoomLevel: Double?): HomeInitialCameraCommand? {
        filterResolved = true
        configuredZoom = initialMapZoomLevel?.takeIf { it.isFinite() && it in MIN_ZOOM..MAX_ZOOM }
        return commandIfReady()
    }

    fun onUserGesture() {
        consumed = true
    }

    private fun commandIfReady(): HomeInitialCameraCommand? {
        if (consumed || !mapReady) return null
        val target = initialTarget ?: return null
        if (!restored && !filterResolved) return null
        consumed = true
        return HomeInitialCameraCommand(target = target, zoom = if (restored) null else configuredZoom)
    }

    private companion object {
        const val MIN_ZOOM = 0.0
        const val MAX_ZOOM = 21.0
    }
}

internal class HomeSearchFocusPolicy {
    private var initialSearchConsumed = false

    fun shouldRequestFocus(hasSavedPosition: Boolean): Boolean {
        if (initialSearchConsumed) return false
        initialSearchConsumed = true
        return !hasSavedPosition
    }
}

internal data class HomeMapPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
)

internal fun calculateHomeMapPadding(
    mapHeightPx: Int,
    mapWindowTopPx: Int,
    filterWindowBottomPx: Int,
    sheetVisibleHeightPx: Int,
    edgeMarginPx: Int,
): HomeMapPadding? {
    if (mapHeightPx <= 0) return null
    val margin = edgeMarginPx.coerceAtLeast(0)
    val topOverlap = (filterWindowBottomPx - mapWindowTopPx).coerceAtLeast(0)
    val bottomOverlap = sheetVisibleHeightPx.coerceAtLeast(0)
    val top = topOverlap + margin
    val bottom = bottomOverlap + margin
    if (top + bottom >= mapHeightPx) return null
    return HomeMapPadding(left = margin, top = top, right = margin, bottom = bottom)
}
