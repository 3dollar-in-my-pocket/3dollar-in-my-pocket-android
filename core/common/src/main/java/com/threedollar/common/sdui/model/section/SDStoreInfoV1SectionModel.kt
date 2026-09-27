package com.threedollar.common.sdui.model.section

import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

data class SDStoreInfoV1SectionModel(
    override val sectionId: String?,
    val header: SDHeaderModel?,
    val informationCard: InformationCard?,
    val menuCard: MenuCard?,
    override val style: SDSurfaceStyleModel?
) : SDSectionModel {
    override val type: SDSectionType get() = SDSectionType.INFO_V1

    data class InformationCard(
        val rows: List<Row>?,
        val style: SDSurfaceStyleModel?
    )

    enum class RowType {
        TRAILING_TEXT,
        CHIP_GROUP,
        INLINE_OPTION
    }

    /** 타입별로 채워지는 필드가 다르다. 모르는 [type]은 null 이 되어 렌더링에서 빠진다. */
    data class Row(
        val type: RowType?,
        val label: SDTextModel?,
        val value: SDTextModel?,
        val chips: List<SDChipModel>?,
        val items: List<SelectableText>?
    )

    data class SelectableText(
        val text: SDTextModel?,
        val isSelected: Boolean?
    )

    data class MenuCard(
        val groups: List<MenuGroup>?,
        val style: SDSurfaceStyleModel?
    )

    data class MenuGroup(
        val header: SDChipModel?,
        val items: List<MenuItem>?
    )

    data class MenuItem(
        val primaryText: SDTextModel?,
        val secondaryText: SDTextModel?
    )
}
