package com.zion830.threedollars.ui.storeDetail.displayitem

import com.threedollar.domain.store.model.SessionViewCountRangeModel
import com.threedollar.domain.store.model.StoreDisplayItemModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.model.StoreDisplayTriggerConditionsModel
import com.threedollar.domain.store.model.StoreDisplayTriggerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDisplayItemPolicyTest {

    private fun item(
        type: StoreDisplayItemType,
        isVisible: Boolean = true,
        range: SessionViewCountRangeModel? = null,
    ) = StoreDisplayItemModel(
        itemType = type,
        isVisible = isVisible,
        trigger = StoreDisplayTriggerModel(conditions = StoreDisplayTriggerConditionsModel(sessionViewCountRange = range)),
    )

    // TH-1226 TC21
    @Test
    fun `TH1226_TC21_상세에_처음_보였을때_한번만_모달을_조회한다`() {
        // Given
        val storeId = "118"

        // When
        val firstEntry = StoreDisplayItemRequestPolicy.shouldRequest(storeId, requestedStoreId = null, hasContent = true, hasLocation = true)
        val afterEditReturn = StoreDisplayItemRequestPolicy.shouldRequest(storeId, requestedStoreId = storeId, hasContent = true, hasLocation = true)

        // Then
        assertTrue(firstEntry)
        assertFalse(afterEditReturn)
    }

    // TH-1226 TC21
    @Test
    fun `TH1226_TC21_다른가게로_바뀌면_새가게는_다시_조회한다`() {
        // Given
        val requestedStoreId = "118"

        // When
        val otherStore = StoreDisplayItemRequestPolicy.shouldRequest("120145", requestedStoreId, hasContent = true, hasLocation = true)

        // Then
        assertTrue(otherStore)
    }

    // TH-1226 TC21
    @Test
    fun `TH1226_TC21_상세응답전이나_위치가_없으면_조회하지_않는다`() {
        assertFalse(StoreDisplayItemRequestPolicy.shouldRequest("118", null, hasContent = false, hasLocation = true))
        assertFalse(StoreDisplayItemRequestPolicy.shouldRequest("118", null, hasContent = true, hasLocation = false))
    }

    // TH-1226 TC21
    @Test
    fun `TH1226_TC21_보이는_아는타입이고_세션조회수_조건을_만족하는_모달만_남긴다`() {
        // Given
        val items = listOf(
            item(StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL, range = SessionViewCountRangeModel(min = 1, max = 1)),
            item(StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL, range = SessionViewCountRangeModel(min = 2)),
            item(StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL, isVisible = false),
            item(StoreDisplayItemType.UNKNOWN),
        )

        // When
        val firstView = StoreDisplayItemFilter.eligible(items, viewCount = 1)
        val secondView = StoreDisplayItemFilter.eligible(items, viewCount = 2)

        // Then
        assertEquals(listOf(StoreDisplayItemType.VISIT_CERTIFICATION_INDUCEMENT_MODAL), firstView.map { it.itemType })
        assertEquals(listOf(StoreDisplayItemType.DISAPPEARANCE_INQUIRY_MODAL), secondView.map { it.itemType })
    }
}
