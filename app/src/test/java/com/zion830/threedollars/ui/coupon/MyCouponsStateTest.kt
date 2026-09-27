package com.zion830.threedollars.ui.coupon

import com.threedollar.domain.store.model.IssuedCouponModel
import com.threedollar.domain.store.model.IssuedCouponPageModel
import com.threedollar.domain.store.model.IssuedCouponStatus
import com.zion830.threedollars.ui.coupon.model.CouponDisplay
import com.zion830.threedollars.ui.coupon.model.CouponTab
import com.zion830.threedollars.ui.coupon.model.CouponTabState
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MyCouponsStateTest {

    private fun coupon(
        key: String,
        status: IssuedCouponStatus = IssuedCouponStatus.ISSUED,
        end: String = "2026-10-14T23:59:59",
    ) = IssuedCouponModel(
        issuedKey = key,
        name = "테스트용 쿠폰",
        startDateTime = "2026-09-14T00:00:00",
        endDateTime = end,
        status = status,
        storeId = "12804906",
        storeName = "현식 테스트",
        storeCategoryName = "두쫀쿠",
        storeCategoryImageUrl = null,
    )

    private fun page(keys: IntRange, nextCursor: String?) =
        IssuedCouponPageModel(keys.map { coupon(it.toString()) }, nextCursor)

    // TH-717 TC3
    @Test
    fun `TH717_TC3_다음페이지가_있으면_개수를_더있음으로_표시한다`() {
        // Given
        val more = page(1..20, nextCursor = "MjA=")
        val last = page(1..3, nextCursor = null)

        // When
        val moreCount = CouponDisplay.sectionCount(more)
        val lastCount = CouponDisplay.sectionCount(last)

        // Then
        assertEquals(CouponDisplay.SectionCount(count = 20, hasMore = true), moreCount)
        assertEquals(CouponDisplay.SectionCount(count = 3, hasMore = false), lastCount)
    }

    // TH-717 TC6
    @Test
    fun `TH717_TC6_사용가능탭은_ISSUED_지난탭은_USED와_EXPIRED를_조회한다`() {
        // Given
        val tabs = CouponTab.entries

        // When
        val statuses = tabs.associateWith { it.statuses }

        // Then
        assertEquals(listOf(IssuedCouponStatus.ISSUED), statuses[CouponTab.AVAILABLE])
        assertEquals(listOf(IssuedCouponStatus.USED, IssuedCouponStatus.EXPIRED), statuses[CouponTab.PAST])
        assertEquals(CouponTab.AVAILABLE, tabs.first())
    }

    // TH-717 TC8
    @Test
    fun `TH717_TC8_종료일까지_남은날을_세고_종료당일은_0_지난쿠폰은_null이다`() {
        // Given
        val today = LocalDate.of(2026, 10, 10)

        // When
        val fourDaysLeft = CouponDisplay.remainingDays(coupon("1", end = "2026-10-14T23:59:59"), today)
        val endsToday = CouponDisplay.remainingDays(coupon("2", end = "2026-10-10T23:59:59"), today)
        val ended = CouponDisplay.remainingDays(coupon("3", end = "2026-10-09T23:59:59"), today)

        // Then
        assertEquals(4L, fourDaysLeft)
        assertEquals(0L, endsToday)
        assertNull(ended)
    }

    // TH-717 TC8
    @Test
    fun `TH717_TC8_유효기간을_yyyy_MM_dd로_표시한다`() {
        // Given
        val coupon = coupon("1")

        // When
        val period = CouponDisplay.periodDates(coupon)

        // Then
        assertEquals("2026.09.14" to "2026.10.14", period)
    }

    // TH-717 TC9
    @Test
    fun `TH717_TC9_다음페이지를_이어붙이고_커서가_없으면_더_요청하지_않는다`() {
        // Given
        val first = CouponTabState().appendPage(page(1..20, nextCursor = "MjA="))

        // When
        val last = first.appendPage(page(21..22, nextCursor = null))

        // Then
        assertTrue(first.canLoadMore)
        assertFalse(first.copy(isLoading = true).canLoadMore)
        assertEquals(22, last.coupons.size)
        assertFalse(last.canLoadMore)
    }

    // TH-717 TC12, TC14
    @Test
    fun `TH717_TC12_쿠폰을_쓰면_두탭을_비워_첫페이지부터_다시_불러온다`() {
        // Given
        val state = MyCouponsUiState()
            .updateTab(CouponTab.AVAILABLE) { it.appendPage(page(1..2, nextCursor = null)) }
            .updateTab(CouponTab.PAST) { it.appendPage(page(3..3, nextCursor = null)) }

        // When
        val reset = state.resetTabs()

        // Then
        assertTrue(CouponTab.entries.all { reset.tab(it) == CouponTabState() })
    }

    // TH-717 TC16
    @Test
    fun `TH717_TC16_첫조회가_끝나고_0장이면_빈뷰다`() {
        // Given
        val loading = CouponTabState(isLoading = true)

        // When
        val empty = loading.appendPage(IssuedCouponPageModel(emptyList(), nextCursor = null))

        // Then
        assertFalse(loading.isEmpty)
        assertTrue(empty.isEmpty)
    }
}
