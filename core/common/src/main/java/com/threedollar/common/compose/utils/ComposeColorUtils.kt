package com.threedollar.common.compose.utils

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

fun String.toColor(
    fallback: Color = Color.Transparent
): Color = Color(toColorInt())
