package com.zion830.threedollars.ui.home.data

import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeCurationAdPolicyTest {

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_실응답의_50dp섹션광고는_320x50배너로_표시한다`() {
        // Given
        val card = HomeCurationTestFixtures.section().items.filterIsInstance<SDHomeCurationItemModel.AdMob>().single().card

        // When
        val dimensions = HomeCurationAdPolicy.dimensions(serverHeightDp = card.height, availableWidthDp = 360)
        val narrow = HomeCurationAdPolicy.dimensions(serverHeightDp = card.height, availableWidthDp = 319)

        // Then
        assertEquals(50, card.height)
        assertEquals(HomeCurationAdDimensions(widthDp = 320, heightDp = 50), dimensions)
        assertNull(narrow)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_실응답의_100dp캐러셀광고는_320x100배너로_표시한다`() {
        // Given
        val card = HomeCurationTestFixtures.cards().cards.filterIsInstance<SDAdMobCardModel>().single()

        // When
        val dimensions = HomeCurationAdPolicy.dimensions(serverHeightDp = card.height, availableWidthDp = 360)

        // Then
        assertEquals(100, card.height)
        assertEquals(HomeCurationAdDimensions(widthDp = 320, heightDp = 100), dimensions)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_서버높이가_99면_50배너를_100이상이면_100배너를_선택한다`() {
        // Given
        val availableWidthDp = 360

        // When
        val belowLargeBanner = HomeCurationAdPolicy.dimensions(serverHeightDp = 99, availableWidthDp = availableWidthDp)
        val aboveLargeBanner = HomeCurationAdPolicy.dimensions(serverHeightDp = 150, availableWidthDp = availableWidthDp)

        // Then
        assertEquals(HomeCurationAdDimensions(320, 50), belowLargeBanner)
        assertEquals(HomeCurationAdDimensions(320, 100), aboveLargeBanner)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_사용가능폭이_정확히_320dp이면_배너를_표시한다`() {
        // Given
        val card = HomeCurationTestFixtures.cards().cards.filterIsInstance<SDAdMobCardModel>().single()

        // When
        val dimensions = HomeCurationAdPolicy.dimensions(serverHeightDp = card.height, availableWidthDp = 320)

        // Then
        assertEquals(HomeCurationAdDimensions(320, 100), dimensions)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_사용가능폭이_320dp보다_좁으면_광고를_생략한다`() {
        // Given
        val card = HomeCurationTestFixtures.cards().cards.filterIsInstance<SDAdMobCardModel>().single()

        // When
        val narrow = HomeCurationAdPolicy.dimensions(serverHeightDp = card.height, availableWidthDp = 319)
        val imageCardWidth = HomeCurationAdPolicy.dimensions(serverHeightDp = card.height, availableWidthDp = 100)

        // Then
        assertNull(narrow)
        assertNull(imageCardWidth)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_지원배너보다_낮거나_알수없는높이면_광고를_생략한다`() {
        // Given
        val availableWidthDp = 360

        // When
        val short = HomeCurationAdPolicy.dimensions(serverHeightDp = 49, availableWidthDp = availableWidthDp)
        val zero = HomeCurationAdPolicy.dimensions(serverHeightDp = 0, availableWidthDp = availableWidthDp)
        val negative = HomeCurationAdPolicy.dimensions(serverHeightDp = -1, availableWidthDp = availableWidthDp)

        // Then
        assertNull(short)
        assertNull(zero)
        assertNull(negative)
    }
}
