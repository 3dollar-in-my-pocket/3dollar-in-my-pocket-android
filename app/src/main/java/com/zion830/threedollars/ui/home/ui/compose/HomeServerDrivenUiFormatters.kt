package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.ui.text.font.FontWeight

internal fun String?.toServerDrivenFontWeight(fallback: FontWeight): FontWeight {
    return when (this?.trim()?.uppercase()?.replace("-", "_")?.replace(" ", "_")) {
        "BLACK", "900" -> FontWeight.Black
        "EXTRA_BOLD", "EXTRABOLD", "800" -> FontWeight.ExtraBold
        "BOLD", "700" -> FontWeight.Bold
        "SEMI_BOLD", "SEMIBOLD", "600" -> FontWeight.SemiBold
        "MEDIUM", "500" -> FontWeight.Medium
        "NORMAL", "REGULAR", "400" -> FontWeight.Normal
        "LIGHT", "300" -> FontWeight.Light
        else -> fallback
    }
}
