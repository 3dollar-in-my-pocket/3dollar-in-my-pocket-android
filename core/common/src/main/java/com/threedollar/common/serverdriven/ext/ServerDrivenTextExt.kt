package com.threedollar.common.serverdriven.ext

import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDTextModel

fun SDTextModel?.displayText(): String {
    val value = this?.text.orEmpty()
    return if (this?.isHtml == true || value.hasHtmlTag()) {
        value.toServerDrivenPlainText()
    } else {
        value
    }
}

fun SDChipModel?.displayText(): String {
    if (this == null) return ""
    return listOf(text.displayText(), additionalText.displayText())
        .filter { it.isNotBlank() }
        .joinToString(" ")
}

fun String.toServerDrivenPlainText(): String {
    return stripHtmlTags().decodeHtmlEntities()
}

private fun String.hasHtmlTag(): Boolean = HTML_TAG_REGEX.containsMatchIn(this)

private fun String.stripHtmlTags(): String {
    return replace(HTML_LINE_BREAK_REGEX, "\n")
        .replace(HTML_TAG_REGEX, "")
}

private fun String.decodeHtmlEntities(): String {
    return replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace(HTML_DECIMAL_ENTITY_REGEX) { match ->
            match.groupValues[1].toIntOrNull()?.toCodePointString() ?: match.value
        }
        .replace(HTML_HEX_ENTITY_REGEX) { match ->
            match.groupValues[1].toIntOrNull(16)?.toCodePointString() ?: match.value
        }
}

private fun Int.toCodePointString(): String {
    return runCatching { String(Character.toChars(this)) }.getOrDefault("")
}

private val HTML_TAG_REGEX = Regex("<[^>]+>")
private val HTML_LINE_BREAK_REGEX = Regex("(?i)<br\\s*/?>")
private val HTML_DECIMAL_ENTITY_REGEX = Regex("&#(\\d+);")
private val HTML_HEX_ENTITY_REGEX = Regex("&#x([0-9a-fA-F]+);")
