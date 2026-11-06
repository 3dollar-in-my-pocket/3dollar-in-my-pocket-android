package com.zion830.threedollars.ui.my.page

import com.threedollar.domain.creator.model.CreatorGroupModel
import com.zion830.threedollars.ui.my.page.data.TeamRow
import org.junit.Assert.assertEquals
import org.junit.Test

class TeamRowTest {

    // TH-1546 TC2
    @Test
    fun `TH1546_TC2_팀원이_없는_역할은_목록에서_제외하고_순서는_유지한다`() {
        // Given
        val groups = listOf(
            CreatorGroupModel(role = "iOS", creatorNames = listOf("유현식")),
            CreatorGroupModel(role = "Marketing", creatorNames = emptyList()),
            CreatorGroupModel(role = "Product Design", creatorNames = listOf("양민설", "조혜원")),
        )

        // When
        val rows = TeamRow.from(groups)

        // Then
        assertEquals(listOf("iOS", "Product Design"), rows.map { it.role })
        assertEquals(listOf("양민설", "조혜원"), rows.last().members)
    }
}
