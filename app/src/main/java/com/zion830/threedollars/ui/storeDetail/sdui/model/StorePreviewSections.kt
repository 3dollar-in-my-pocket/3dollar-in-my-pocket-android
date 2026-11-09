package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel

/**
 * 홈 미리보기 시트(`/preview` 응답)에서 그릴 섹션을 고른다.
 *
 * 미리보기는 PREVIEW 와 AD_MOB 만 그리고 순서는 서버가 내려준 그대로 둔다(어드민에서 광고 위치를 바꿀 수 있다).
 * 그 밖의 섹션(아직 지원하지 않는 PREVIEW_MORE 등)은 빼서 기존 미리보기를 깨지 않는다.
 */
object StorePreviewSections {

    /** 미리보기 광고 노출 로그 키 접두. 상세 AD_MOB 과 sectionId 가 같아도 노출 중복 제거에서 서로를 막지 않게 한다. */
    const val IMPRESSION_KEY_PREFIX = "preview-"

    fun from(sections: List<SDSectionModel>?): List<SDSectionModel> {
        var hasPreview = false
        return sections.orEmpty().filter { section ->
            when (section) {
                is SDStorePreviewSectionModel -> !hasPreview.also { hasPreview = true }
                is SDStoreAdmobSectionModel -> section.cards?.firstOrNull() != null
                else -> false
            }
        }
    }

    fun impressionLogs(index: Int, section: SDStoreAdmobSectionModel): List<Pair<String, SDLogModel>> =
        StoreDetailSectionImpressions.logs("$index-${section.type}", section)
            .map { (key, log) -> "$IMPRESSION_KEY_PREFIX$key" to log }
}
