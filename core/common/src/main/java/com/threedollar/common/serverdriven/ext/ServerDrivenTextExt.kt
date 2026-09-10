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
    val parents = mutableListOf<Pair<String, SDStyledTextSegment>>()
    var current = fallback
    var cursor = 0

    fun append(raw: String) {
        val content = raw.decodeHtmlEntities()
        if (content.isEmpty()) return
        val segment = current.copy(text = content)
        val previous = segments.lastOrNull()
        if (previous != null && previous.copy(text = content) == segment) {
            segments[segments.lastIndex] = previous.copy(text = previous.text + content)
        } else {
            segments += segment
        }
    }

    HTML_TAG_REGEX.findAll(text).forEach { token ->
        append(text.substring(cursor, token.range.first))
        cursor = token.range.last + 1
        val tag = HTML_ELEMENT_REGEX.matchEntire(token.value) ?: return@forEach
        val name = tag.groupValues[2].lowercase()
        val closing = tag.groupValues[1].isNotEmpty()
        if (closing) {
            val parentIndex = parents.indexOfLast { it.first == name }
            if (parentIndex >= 0) {
                current = parents[parentIndex].second
                while (parents.size > parentIndex) parents.removeAt(parents.lastIndex)
            }
        } else if (name == "br") {
            append("\n")
        } else if (!token.value.endsWith("/>") && name !in HTML_VOID_ELEMENTS) {
            parents += name to current
            val styles = STYLE_ATTRIBUTE_REGEX.find(tag.groupValues[3])?.groupValues?.getOrNull(1).toCssProperties()
            current = current.copy(
                fontSizePx = styles["font-size"]?.removeSuffix("px")?.trim()?.toDoubleOrNull()
                    ?.takeIf { it.isFinite() && it >= 0 }?.toInt() ?: current.fontSizePx,
                fontWeight = styles["font-weight"]?.let { weight ->
                    when (weight.lowercase()) {
                        "bold" -> 700
                        "normal" -> 400
                        else -> weight.toIntOrNull()
                    }
                } ?: if (name == "b" || name == "strong") 700 else current.fontWeight,
                fontColor = styles["color"] ?: current.fontColor,
            )
        }
    }
    append(text.substring(cursor))
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

private fun String.decodeHtmlEntities(): String = HTML_ENTITY_REGEX.replace(this) { match ->
    when (val entity = match.groupValues[1]) {
        "nbsp" -> " "
        "amp" -> "&"
        "lt" -> "<"
        "gt" -> ">"
        "quot" -> "\""
        "apos", "#39" -> "'"
        else -> {
            val codePoint = when {
                entity.startsWith("#x", ignoreCase = true) -> entity.substring(2).toIntOrNull(16)
                entity.startsWith("#") -> entity.substring(1).toIntOrNull()
                else -> null
            }
            codePoint?.takeIf(Character::isValidCodePoint)
                ?.let { String(Character.toChars(it)) } ?: match.value
        }
    }
}

private val HTML_TAG_REGEX = Regex("<[^>]+>")
private val HTML_ELEMENT_REGEX = Regex("(?is)<\\s*(/?)\\s*([a-z][a-z0-9]*)\\b([^>]*)>")
private val HTML_VOID_ELEMENTS = setOf("area", "base", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr")
private val STYLE_ATTRIBUTE_REGEX = Regex("(?is)\\bstyle\\s*=\\s*[\"']([^\"']*)[\"']")
private val HTML_LINE_BREAK_REGEX = Regex("(?i)<br\\s*/?>")
private val HTML_ENTITY_REGEX = Regex("&(#x[0-9a-fA-F]+|#X[0-9a-fA-F]+|#[0-9]+|[a-zA-Z]+);")
