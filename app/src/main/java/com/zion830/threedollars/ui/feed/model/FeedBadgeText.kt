package com.zion830.threedollars.ui.feed.model

import com.threedollar.common.sdui.model.element.SDTextModel
import java.text.BreakIterator

/**
 * 카테고리 배지 문구("📢 새로운 가게 소식이 올라왔어요!")에서 맨 앞 이모지를 떼어 낸다.
 * 이모지를 글자와 한 줄로 그리면 기기 이모지 폰트의 줄 높이 때문에 글자가 아래로 처지므로(삼성 기기),
 * 이모지는 아이콘처럼 따로 그리고 글자와 가운데 정렬한다. HTML 문구는 구조를 알 수 없어 그대로 둔다.
 */
object FeedBadgeText {

    data class Parts(val leadingEmoji: String?, val text: SDTextModel)

    fun split(model: SDTextModel): Parts {
        val raw = model.text.orEmpty()
        if (model.isHtml == true || raw.isBlank()) return Parts(null, model)

        val iterator = BreakIterator.getCharacterInstance().apply { setText(raw) }
        val end = iterator.next().takeIf { it != BreakIterator.DONE } ?: return Parts(null, model)
        val first = raw.substring(0, end)
        if (!first.isEmoji()) return Parts(null, model)

        val rest = raw.substring(end).trimStart()
        if (rest.isEmpty()) return Parts(null, model)
        return Parts(leadingEmoji = first, text = model.copy(text = rest))
    }

    private fun String.isEmoji(): Boolean {
        val codePoint = codePointAt(0)
        return codePoint in EMOJI_SUPPLEMENTARY || codePoint in EMOJI_MISC_SYMBOLS ||
            Character.getType(codePoint) == Character.OTHER_SYMBOL.toInt() && codePoint > ASCII_MAX
    }

    private val EMOJI_SUPPLEMENTARY = 0x1F000..0x1FAFF
    private val EMOJI_MISC_SYMBOLS = 0x2600..0x27BF
    private const val ASCII_MAX = 0x7F
}
