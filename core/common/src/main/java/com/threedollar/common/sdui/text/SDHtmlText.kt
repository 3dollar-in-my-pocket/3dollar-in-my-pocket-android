package com.threedollar.common.sdui.text

/**
 * 서버 HTML 텍스트의 한 구간. 스타일 값이 없으면 null 이고, 렌더러의 기본값을 쓴다.
 *
 * @property fontSize `font-size` px 값 (dp 로 취급한다)
 * @property fontWeight `font-weight` 숫자 값 (`bold`/`<b>`는 700)
 * @property color `color` 값 (`#RRGGBB`)
 */
data class SDTextRun(
    val text: String,
    val fontSize: Float? = null,
    val fontWeight: Int? = null,
    val color: String? = null
)

/**
 * 서버가 내려주는 `<span style="font-size:14px; font-weight:600; color:#0F0F0F">` 형태의 HTML 을
 * 스타일 구간 목록으로 바꾼다. `HtmlCompat` 은 span 의 font-size·font-weight 를 무시하기 때문에 직접 파싱한다.
 *
 * 지원: `span`(style), `b`/`strong`, `font`(color), `br`, HTML 엔티티. 그 외 태그는 무시하고 내용만 남긴다.
 */
object SDHtmlText {

    fun parse(html: String?): List<SDTextRun> {
        if (html.isNullOrEmpty()) return emptyList()
        val runs = mutableListOf<SDTextRun>()
        val styleStack = ArrayDeque<StackEntry>()
        var cursor = 0

        fun currentStyle(): SDTextRun = styleStack.lastOrNull()?.style ?: SDTextRun(text = "")

        fun appendText(raw: String) {
            if (raw.isEmpty()) return
            val decoded = decodeEntities(raw)
            val style = currentStyle()
            val last = runs.lastOrNull()
            if (last != null && last.copy(text = "") == style.copy(text = "")) {
                runs[runs.lastIndex] = last.copy(text = last.text + decoded)
            } else {
                runs += style.copy(text = decoded)
            }
        }

        TAG_REGEX.findAll(html).forEach { match ->
            appendText(html.substring(cursor, match.range.first))
            cursor = match.range.last + 1

            val isClosing = match.groupValues[1] == "/"
            val tagName = match.groupValues[2].lowercase()
            val attributes = match.groupValues[3]
            when {
                tagName == "br" -> appendText("\n")
                isClosing -> {
                    val index = styleStack.indexOfLast { it.tagName == tagName }
                    if (index >= 0) {
                        while (styleStack.size > index) styleStack.removeLast()
                    }
                }
                attributes.trimEnd().endsWith("/") -> Unit
                else -> styleStack.addLast(StackEntry(tagName, currentStyle().merge(tagName, attributes)))
            }
        }
        appendText(html.substring(cursor))
        return runs
    }

    fun plainText(html: String?): String = parse(html).joinToString(separator = "") { it.text }

    private fun SDTextRun.merge(tagName: String, attributes: String): SDTextRun {
        var result = this
        if (tagName == "b" || tagName == "strong") {
            result = result.copy(fontWeight = BOLD_WEIGHT)
        }
        COLOR_ATTRIBUTE_REGEX.find(attributes)?.let { result = result.copy(color = it.groupValues[1].trim()) }
        val style = STYLE_ATTRIBUTE_REGEX.find(attributes)?.groupValues?.get(1) ?: return result
        style.split(';').forEach { declaration ->
            val key = declaration.substringBefore(':', "").trim().lowercase()
            val value = declaration.substringAfter(':', "").trim()
            when (key) {
                "font-size" -> value.removeSuffix("px").trim().toFloatOrNull()?.let { result = result.copy(fontSize = it) }
                "font-weight" -> parseWeight(value)?.let { result = result.copy(fontWeight = it) }
                "color" -> if (value.isNotEmpty()) result = result.copy(color = value)
            }
        }
        return result
    }

    private fun parseWeight(value: String): Int? = when (value.lowercase()) {
        "bold", "bolder" -> BOLD_WEIGHT
        "normal" -> NORMAL_WEIGHT
        else -> value.toIntOrNull()
    }

    private fun decodeEntities(text: String): String = text
        .replace(DECIMAL_ENTITY_REGEX) { it.groupValues[1].toIntOrNull()?.toCodePointString() ?: it.value }
        .replace(HEX_ENTITY_REGEX) { it.groupValues[1].toIntOrNull(16)?.toCodePointString() ?: it.value }
        .replace("&nbsp;", " ")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'")
        .replace("&amp;", "&")

    private fun Int.toCodePointString(): String? = runCatching { String(Character.toChars(this)) }.getOrNull()

    private data class StackEntry(val tagName: String, val style: SDTextRun)

    private const val BOLD_WEIGHT = 700
    private const val NORMAL_WEIGHT = 400
    private val TAG_REGEX = Regex("<(/?)([a-zA-Z][a-zA-Z0-9]*)([^>]*)>")
    private val STYLE_ATTRIBUTE_REGEX = Regex("style\\s*=\\s*[\"']([^\"']*)[\"']", RegexOption.IGNORE_CASE)
    private val COLOR_ATTRIBUTE_REGEX = Regex("\\scolor\\s*=\\s*[\"']([^\"']*)[\"']", RegexOption.IGNORE_CASE)
    private val DECIMAL_ENTITY_REGEX = Regex("&#(\\d+);")
    private val HEX_ENTITY_REGEX = Regex("&#[xX]([0-9a-fA-F]+);")
}
