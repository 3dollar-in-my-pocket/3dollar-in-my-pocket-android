package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel

/**
 * 가게 상세 SDUI 섹션 타입. JSON `type` 값과 이름이 같다.
 * 앱이 모르는 타입은 [UNKNOWN]으로 떨어지고 렌더링에서 빠진다.
 */
enum class SDSectionType {
    PREVIEW,
    TAB,
    EDIT,
    COUPON,
    VISIT,
    POST,
    IMAGE,
    APPEARANCE_DAY,
    INFO_V1,
    INFO_V2,
    RELATED_STORES,
    CTA,
    REVIEW,
    CALLOUT,
    AD_MOB,
    MARGIN,
    UNKNOWN;

    companion object {
        fun from(raw: String?): SDSectionType = entries.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}

/**
 * Gson 은 생성자를 거치지 않고 객체를 만들기 때문에 [type]은 backing field 없이 getter 로만 구현한다.
 */
interface SDSectionModel {
    val type: SDSectionType
    val sectionId: String? get() = null
    val style: SDSurfaceStyleModel? get() = null
}

data class SDUnknownSectionModel(
    val rawType: String?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.UNKNOWN
}

data class SDRelatedStoresSectionModel(
    val header: SDHeaderModel?,
    val cards: List<SDCardModel>,
    val reference: List<Reference>?,
    override val sectionId: String? = null,
    val impressionLog: SDLogModel? = null,
    override val style: SDSurfaceStyleModel? = null
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.RELATED_STORES

    data class Reference(
        val type: String?,
        val experimentKey: String?,
        val variant: String?
    )
}
