package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDSectionType

/**
 * `stores/{storeId}#{fragment}` 링크와 fragment → 섹션 위치 해석.
 *
 * 해석 순서: `sectionId` 일치(대소문자 무시) → 별칭(`home`/`preview`, `info`, `image(s)`, `review(s)`) → 타입명.
 */
object StoreSectionFragment {

    data class Link(
        val storeId: String,
        val fragment: String?
    )

    private const val STORES_PATH = "stores"

    /**
     * `/stores/120145#INFO`, `dollars://stores/120145#review` 같은 링크를 해석한다. 가게 상세 섹션 링크가 아니면 null.
     */
    fun parseLink(link: String?): Link? {
        if (link.isNullOrBlank()) return null
        val fragment = link.substringAfter('#', missingDelimiterValue = "").takeIf { it.isNotBlank() }
        val withoutFragment = link.substringBefore('#').substringBefore('?')
        val path = withoutFragment.substringAfter("://", missingDelimiterValue = withoutFragment)
        val segments = path.split('/').filter { it.isNotBlank() }
        val storesIndex = segments.indexOf(STORES_PATH)
        if (storesIndex < 0) return null
        val storeId = segments.getOrNull(storesIndex + 1)?.takeIf { id -> id.all(Char::isDigit) } ?: return null
        return Link(storeId = storeId, fragment = fragment)
    }

    fun resolveIndex(sections: List<SDSectionModel>, fragment: String?): Int? {
        val key = fragment?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        sections.indexOfFirst { it.sectionId.equals(key, ignoreCase = true) }
            .takeIf { it >= 0 }
            ?.let { return it }
        val candidates = candidateTypes(key)
        return sections.indexOfFirst { it.type in candidates }.takeIf { it >= 0 }
    }

    private fun candidateTypes(fragment: String): Set<SDSectionType> = when (fragment.lowercase()) {
        "home", "preview" -> setOf(SDSectionType.PREVIEW)
        "info", "info_v1", "info_v2" -> setOf(SDSectionType.INFO_V1, SDSectionType.INFO_V2)
        "image", "images" -> setOf(SDSectionType.IMAGE)
        "review", "reviews" -> setOf(SDSectionType.REVIEW)
        else -> SDSectionType.from(fragment.uppercase()).takeIf { it != SDSectionType.UNKNOWN }?.let(::setOf).orEmpty()
    }
}
