package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

data class SDStoreInfoV2SectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val imageGallery: ImageGallery?,
    val detailCard: DetailCard?,
    val accountCards: List<AccountCard>?,
    val menuListCard: MenuListCard?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.INFO_V2

    data class ImageGallery(
        val images: List<SDImageModel>?
    )

    data class DetailCard(
        val rows: List<DetailRow>?,
        val style: SDSurfaceStyleModel?
    )

    enum class DetailRowType {
        LINK,
        TEXT
    }

    /** [DetailRowType.LINK]는 label·value·link, [DetailRowType.TEXT]는 title·body 를 쓴다. */
    data class DetailRow(
        val type: DetailRowType?,
        val label: SDTextModel?,
        val value: SDTextModel?,
        val link: SDLink?,
        val title: SDTextModel?,
        val body: SDTextModel?
    )

    data class AccountCard(
        val title: SDTextModel?,
        val account: SDChipModel?,
        val copyButton: SDButtonModel?,
        val style: SDSurfaceStyleModel?
    )

    data class MenuListCard(
        val items: List<MenuItem>?,
        val style: SDSurfaceStyleModel?
    )

    data class MenuItem(
        val image: SDImageModel?,
        val primaryText: SDTextModel?,
        val secondaryText: SDTextModel?
    )
}
