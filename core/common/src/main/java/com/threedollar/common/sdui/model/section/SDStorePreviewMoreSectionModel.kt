package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

/**
 * 미리보기(`/preview`) 응답 하단의 다음 섹션 티저. 시트 아래에 일부만 보여 상세 펼침을 유도한다.
 */
data class SDStorePreviewMoreSectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null,
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.PREVIEW_MORE
}
