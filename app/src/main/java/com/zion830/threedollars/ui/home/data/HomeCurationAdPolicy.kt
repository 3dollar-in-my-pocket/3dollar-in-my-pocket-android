package com.zion830.threedollars.ui.home.data

data class HomeCurationAdDimensions(
    val widthDp: Int,
    val heightDp: Int,
)

object HomeCurationAdPolicy {
    fun dimensions(serverHeightDp: Int, availableWidthDp: Int): HomeCurationAdDimensions? {
        if (availableWidthDp < 320 || serverHeightDp < 50) return null
        return HomeCurationAdDimensions(widthDp = 320, heightDp = if (serverHeightDp >= 100) 100 else 50)
    }
}
