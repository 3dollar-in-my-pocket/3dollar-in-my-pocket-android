package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.common.sdui.model.section.SDStoreMarginSectionModel
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.IMAGE_INDEX
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.INFO_INDEX
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.PREVIEW_INDEX
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.REVIEW_INDEX
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.sections
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreSectionFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StoreSectionFragmentTest {

    // TH-1226 TC18
    @Test
    fun `TH1226_TC18_stores딥링크에서_가게id와_fragment를_읽는다`() {
        // Given
        val schemeLink = "dollars-dev://stores/120145#review"
        val relativeLink = "/stores/120145#INFO"

        // When
        val fromScheme = StoreSectionFragment.parseLink(schemeLink)
        val fromRelative = StoreSectionFragment.parseLink(relativeLink)

        // Then
        assertEquals(StoreSectionFragment.Link("120145", "review"), fromScheme)
        assertEquals(StoreSectionFragment.Link("120145", "INFO"), fromRelative)
    }

    // TH-1226 TC18
    @Test
    fun `TH1226_TC18_review_fragment는_리뷰섹션_위치로_해석된다`() {
        // Given
        val fragment = "review"

        // When
        val index = StoreSectionFragment.resolveIndex(sections, fragment)

        // Then
        assertEquals(REVIEW_INDEX, index)
    }

    // TH-1226 TC9
    @Test
    fun `TH1226_TC9_탭링크의_별칭fragment를_섹션위치로_해석한다`() {
        // Given
        val aliases = mapOf("home" to PREVIEW_INDEX, "preview" to PREVIEW_INDEX, "info" to INFO_INDEX, "images" to IMAGE_INDEX, "reviews" to REVIEW_INDEX)

        // When
        val resolved = aliases.keys.associateWith { StoreSectionFragment.resolveIndex(sections, it) }

        // Then
        assertEquals(aliases, resolved)
    }

    // TH-1226 TC9
    @Test
    fun `TH1226_TC9_sectionId가_일치하면_타입별칭보다_우선한다`() {
        // Given
        val withCustomId = sections + SDStoreMarginSectionModel(sectionId = "images", height = 8, style = null)

        // When
        val index = StoreSectionFragment.resolveIndex(withCustomId, "images")

        // Then
        assertEquals(withCustomId.lastIndex, index)
    }

    @Test
    fun `모르는_fragment나_가게상세가_아닌_링크는_null이다`() {
        assertNull(StoreSectionFragment.resolveIndex(sections, "unknown-fragment"))
        assertNull(StoreSectionFragment.parseLink("/reviewList?storeId=1"))
        assertNull(StoreSectionFragment.parseLink("dollars://stores/abc#review"))
    }
}
