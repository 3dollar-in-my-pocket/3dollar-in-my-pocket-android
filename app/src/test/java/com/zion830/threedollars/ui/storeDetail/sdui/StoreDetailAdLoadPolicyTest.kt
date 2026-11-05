package com.zion830.threedollars.ui.storeDetail.sdui

import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailAdLoadPolicy
import android.view.View
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailAdLoadPolicyTest {

    // TH-1465 TC1
    @Test
    fun `TH1465_TC1_홈시트_tip에서_상세가_미리그려진_상태면_임베드를_로드하지_않는다`() {
        // Given
        val isDisplayed = false

        // When
        val canLoad = StoreDetailAdLoadPolicy.next(
            canLoad = StoreDetailAdLoadPolicy.initial(isDisplayed),
            isDisplayed = isDisplayed,
        )

        // Then
        assertFalse(canLoad)
    }

    // TH-1465 TC2
    @Test
    fun `TH1465_TC2_상세가_보이는_순간_임베드를_로드한다`() {
        // Given
        val beforeDisplayed = StoreDetailAdLoadPolicy.initial(isDisplayed = false)

        // When
        val canLoad = StoreDetailAdLoadPolicy.next(canLoad = beforeDisplayed, isDisplayed = true)

        // Then
        assertTrue(canLoad)
    }

    // TH-1465 TC3
    @Test
    fun `TH1465_TC3_같은가게에서_tip으로_다시_내려도_로드한_임베드를_유지한다`() {
        // Given
        val displayedOnce = StoreDetailAdLoadPolicy.next(
            canLoad = StoreDetailAdLoadPolicy.initial(isDisplayed = false),
            isDisplayed = true,
        )

        // When
        val afterCollapse = StoreDetailAdLoadPolicy.next(canLoad = displayedOnce, isDisplayed = false)

        // Then
        assertTrue(afterCollapse)
    }

    // TH-1465 TC4
    @Test
    fun `TH1465_TC4_독립_가게상세_화면은_처음부터_임베드를_로드한다`() {
        // Given
        val isDisplayed = true

        // When
        val canLoad = StoreDetailAdLoadPolicy.initial(isDisplayed)

        // Then
        assertTrue(canLoad)
    }

    // TH-1465 TC5
    @Test
    fun `TH1465_TC5_다른가게를_열면_tip_기준으로_다시_막는다`() {
        // Given: 이전 가게에서는 상세가 보여 임베드를 로드했다
        val previousStore = StoreDetailAdLoadPolicy.next(
            canLoad = StoreDetailAdLoadPolicy.initial(isDisplayed = false),
            isDisplayed = true,
        )
        assertTrue(previousStore)

        // When: 새 가게는 초기값부터 다시 시작한다 (라우트가 storeId 로 상태를 다시 만든다)
        val newStore = StoreDetailAdLoadPolicy.initial(isDisplayed = false)

        // Then
        assertFalse(newStore)
    }

    // TH-1571 TC3
    @Test
    fun `TH1571_TC3_미리보기_상태에서는_배너를_INVISIBLE로_둔다`() {
        // Given
        val isDisplayed = false

        // When
        val visibility = StoreDetailAdLoadPolicy.bannerVisibility(isDisplayed)

        // Then
        assertEquals(View.INVISIBLE, visibility)
    }

    // TH-1571 TC4
    @Test
    fun `TH1571_TC4_상세가_보이면_미리_로드한_배너를_VISIBLE로_바꾼다`() {
        // Given
        val isDisplayed = true

        // When
        val visibility = StoreDetailAdLoadPolicy.bannerVisibility(isDisplayed)

        // Then
        assertEquals(View.VISIBLE, visibility)
    }
}
