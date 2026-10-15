package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.element.SDLogModel

data class SDStoreCtaSectionModel(
    override val sectionId: String?,
    val content: Content?,
    override val style: SDSurfaceStyleModel?,
    override val impressionLog: SDLogModel? = null
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.CTA

    data class Content(
        val title: SDTextModel?,
        val subTitle: SDTextModel?,
        val footerLeftButton: SDButtonModel?
    )
}
