package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDSectionType
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewMoreSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel

/**
 * 홈 미리보기 시트(`/preview` 응답)에서 그릴 섹션을 고른다.
 *
 * 미리보기는 PREVIEW 와 AD_MOB 을 서버가 내려준 순서 그대로 그린다(어드민에서 광고 위치를 바꿀 수 있다).
 * PREVIEW_MORE 는 시트 하단에 일부만 보이는 티저라 위치와 상관없이 맨 끝에 하나만 둔다(TH-1584).
 * 그 밖의 섹션은 빼서 기존 미리보기를 깨지 않는다.
 */
object StorePreviewSections {

    /** 미리보기 광고 노출 로그 키 접두. 상세 AD_MOB 과 sectionId 가 같아도 노출 중복 제거에서 서로를 막지 않게 한다. */
    const val IMPRESSION_KEY_PREFIX = "preview-"

    fun from(sections: List<SDSectionModel>?): List<SDSectionModel> {
        var hasPreview = false
        val body = sections.orEmpty().filter { section ->
            when (section) {
                is SDStorePreviewSectionModel -> !hasPreview.also { hasPreview = true }
                is SDStoreAdmobSectionModel -> section.cards?.firstOrNull() != null
                else -> false
            }
        }
        val more = sections.orEmpty().filterIsInstance<SDStorePreviewMoreSectionModel>().firstOrNull()
        return if (more != null && hasPreview) body + more else body
    }

    /**
     * 티저를 붙인 미리보기가 부모(시트)에 알릴 높이. 티저는 [maxPeekPx] 까지만 넣어서
     * 시트가 그 위치에 멈추고 나머지는 화면 밖으로 잘리게 한다. 티저가 없으면 본문 높이 그대로다.
     */
    fun peekLayoutHeight(bodyPx: Int, peekPx: Int?, maxPeekPx: Int): Int =
        bodyPx + (peekPx?.coerceIn(0, maxPeekPx) ?: 0)

    /** 상세(full) 화면이 그릴 섹션. 미리보기 전용 티저(PREVIEW_MORE)가 섞여 와도 상세 노출로 잡히지 않게 뺀다. */
    fun detailSections(sections: List<SDSectionModel>?): List<SDSectionModel> =
        sections.orEmpty().filter { it.type != SDSectionType.UNKNOWN && it.type != SDSectionType.PREVIEW_MORE }

    fun impressionLogs(index: Int, section: SDSectionModel): List<Pair<String, SDLogModel>> =
        StoreDetailSectionImpressions.logs("$index-${section.type}", section)
            .map { (key, log) -> "$IMPRESSION_KEY_PREFIX$key" to log }
}
