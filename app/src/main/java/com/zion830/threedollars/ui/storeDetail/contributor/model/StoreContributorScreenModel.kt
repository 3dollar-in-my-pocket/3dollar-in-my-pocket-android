package com.zion830.threedollars.ui.storeDetail.contributor.model

import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel

internal fun List<SDSectionModel>.firstCardsSection(): SDSectionModel.CardsSection? =
    firstOrNull { it is SDSectionModel.CardsSection } as? SDSectionModel.CardsSection

internal fun SDScreenModel.appendFirstCardsSection(
    page: SDSectionModel.CardsSection,
): SDScreenModel {
    var updated = false
    return copy(
        sections = sections.map { section ->
            if (section is SDSectionModel.CardsSection && !updated) {
                updated = true
                section.copy(
                    cards = section.cards + page.cards,
                    cursor = page.cursor,
                )
            } else {
                section
            }
        },
    )
}
