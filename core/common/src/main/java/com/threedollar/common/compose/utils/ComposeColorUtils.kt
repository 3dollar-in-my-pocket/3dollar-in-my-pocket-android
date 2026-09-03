package com.threedollar.common.compose.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import java.lang.Exception

fun String?.toColor(
    fallback: Color = Color.Transparent
): Color = try {
    this?.let { Color(it.normalizeServerDrivenColor().toColorInt()) } ?: fallback
} catch (e: Exception) {
    fallback
}

fun String.normalizeServerDrivenColor(): String {
    val match = SERVER_RGBA_HEX_REGEX.matchEntire(this) ?: return this
    val rgb = match.groupValues[1]
    val alpha = match.groupValues[2]
    return "#$alpha$rgb"
}

private val SERVER_RGBA_HEX_REGEX = Regex("^#([0-9A-Fa-f]{6})([0-9A-Fa-f]{2})$")
