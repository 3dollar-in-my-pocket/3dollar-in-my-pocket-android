package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDRatingChipModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.element.SDToggleActionModel

data class SDStoreReviewSectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val summary: Summary?,
    val cards: List<Card>?,
    val more: SDActionBarModel?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.REVIEW

    data class Summary(
        val title: SDTextModel?,
        val stars: SDRatingChipModel?,
        val rating: SDTextModel?,
        val style: SDSurfaceStyleModel?
    )

    /** [header]가 없으면 블라인드 처리된 리뷰다. */
    data class Card(
        val cardId: String?,
        val header: SDHeaderModel?,
        val metadata: List<SDChipModel>?,
        val stars: SDRatingChipModel?,
        val images: List<SDImageModel>?,
        val body: SDTextModel?,
        val like: SDToggleActionModel?,
        val reply: Reply?,
        val link: SDLink?,
        val style: SDSurfaceStyleModel?,
        val clickLog: SDLogModel?
    ) {
        val isBlinded: Boolean get() = header == null
    }

    data class Reply(
        val header: SDHeaderModel?,
        val body: SDTextModel?,
        val style: SDSurfaceStyleModel?
    )
}
