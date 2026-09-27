package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

/**
 * 서버는 `image`+`text` 형태로 내려주지만 스키마상 `title`·`subTitle`·`footerLeftButton` 형태도 허용한다.
 */
data class SDStoreCalloutSectionModel(
    override val sectionId: String?,
    val content: Content?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.CALLOUT

    data class Content(
        val image: SDImageModel?,
        val text: SDTextModel?,
        val title: SDTextModel?,
        val subTitle: SDTextModel?,
        val footerLeftButton: SDButtonModel?,
        val style: SDSurfaceStyleModel?
    )
}
