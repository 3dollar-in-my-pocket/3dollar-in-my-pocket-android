package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

data class SDStoreCouponSectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val cards: List<Card>?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.COUPON

    data class Card(
        val cardId: String?,
        val badge: SDChipModel?,
        val title: SDTextModel?,
        val subTitle: SDTextModel?,
        val trailingButton: SDButtonModel?,
        val style: SDSurfaceStyleModel?,
        val clickLog: SDLogModel?
    )
}
