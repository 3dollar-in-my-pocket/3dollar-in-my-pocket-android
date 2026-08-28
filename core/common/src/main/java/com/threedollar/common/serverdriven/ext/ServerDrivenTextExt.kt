package com.threedollar.common.serverdriven.ext

import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDTextModel

data class SDStyledTextSegment(
    val text: String,
    val fontSizePx: Int?,
    val fontWeight: Int?,
    val fontColor: String?,
)

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

fun SDTextModel.styledSegments(): List<SDStyledTextSegment> {
    val fallback = SDStyledTextSegment(
        text = displayText(),
        fontSizePx = null,
        fontWeight = fontWeight?.toIntOrNull(),
        fontColor = fontColor,
    )
    if (!isHtml && !HTML_TAG_REGEX.containsMatchIn(text)) return listOf(fallback)

    val segments = mutableListOf<SDStyledTextSegment>()
    var cursor = 0
    HTML_SPAN_REGEX.findAll(text).forEach { match ->
        text.substring(cursor, match.range.first)
            .toServerDrivenPlainText()
            .takeIf(String::isNotEmpty)
            ?.let { plain -> segments += fallback.copy(text = plain) }

        val styles = match.groupValues[1]
            .let(STYLE_ATTRIBUTE_REGEX::find)
            ?.groupValues
            ?.getOrNull(1)
            .toCssProperties()
        val content = match.groupValues[2].toServerDrivenPlainText()
        if (content.isNotEmpty()) {
            segments += SDStyledTextSegment(
                text = content,
                fontSizePx = styles["font-size"]?.removeSuffix("px")?.trim()?.toDoubleOrNull()?.toInt(),
                fontWeight = styles["font-weight"]?.trim()?.toIntOrNull() ?: fallback.fontWeight,
                fontColor = styles["color"]?.trim() ?: fallback.fontColor,
            )
        }
        cursor = match.range.last + 1
    }
    text.substring(cursor)
        .toServerDrivenPlainText()
        .takeIf(String::isNotEmpty)
        ?.let { plain -> segments += fallback.copy(text = plain) }
    return segments.ifEmpty { listOf(fallback) }
}

private fun String?.toCssProperties(): Map<String, String> = this
    ?.split(';')
    ?.mapNotNull { declaration ->
        val separator = declaration.indexOf(':')
        if (separator <= 0) return@mapNotNull null
        declaration.substring(0, separator).trim().lowercase() to
            declaration.substring(separator + 1).trim()
    }
    ?.toMap()
    .orEmpty()

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
private val HTML_SPAN_REGEX = Regex("(?is)<span\\b([^>]*)>(.*?)</span>")
private val STYLE_ATTRIBUTE_REGEX = Regex("(?is)\\bstyle\\s*=\\s*[\"']([^\"']*)[\"']")
private val HTML_LINE_BREAK_REGEX = Regex("(?i)<br\\s*/?>")
private val HTML_DECIMAL_ENTITY_REGEX = Regex("&#(\\d+);")
private val HTML_HEX_ENTITY_REGEX = Regex("&#x([0-9a-fA-F]+);")
