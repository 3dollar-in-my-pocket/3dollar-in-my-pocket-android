package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.ui.text.font.FontWeight
import com.threedollar.common.serverdriven.model.SDTextModel

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

internal fun SDTextModel?.homeTitleForRendering(addBreakOpportunities: Boolean): SDTextModel {
    val model = this ?: return SDTextModel("", false)
    if (model.isHtml) return model
    val renderedText = if (addBreakOpportunities) {
        model.text.withHomeTitleBreakOpportunities()
    } else {
        model.text
    }
    return model.copy(text = renderedText)
}

private fun String.withHomeTitleBreakOpportunities(): String {
    if (length <= 1) return this
    return buildString {
        var index = 0
        while (index < this@withHomeTitleBreakOpportunities.length) {
            val codePoint = this@withHomeTitleBreakOpportunities.codePointAt(index)
            val nextIndex = index + Character.charCount(codePoint)
            append(this@withHomeTitleBreakOpportunities, index, nextIndex)
            if (nextIndex < this@withHomeTitleBreakOpportunities.length &&
                !Character.isWhitespace(codePoint) &&
                !Character.isWhitespace(this@withHomeTitleBreakOpportunities.codePointAt(nextIndex))
            ) {
                append('\u200B')
            }
            index = nextIndex
        }
    }
}
