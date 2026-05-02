package com.threedollar.common.compose.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import java.lang.Exception

fun String?.toColor(
    fallback: Color = Color.Transparent
): Color = try {
    this?.let { Color(it.toColorInt()) } ?: fallback
} catch (e: Exception) {
    fallback
}
