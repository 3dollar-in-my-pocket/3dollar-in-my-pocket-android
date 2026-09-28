package com.zion830.threedollars.ui.coupon.model

import com.threedollar.domain.store.model.IssuedCouponModel
import com.threedollar.domain.store.model.IssuedCouponPageModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

/** 쿠폰 카드 표시값 계산. 화면과 분리해 유닛 테스트한다. */
object CouponDisplay {
    private val PERIOD_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    /** 유효기간 시작·종료일을 `yyyy.MM.dd` 로. 파싱에 실패하면 원문을 그대로 둔다. */
    fun periodDates(coupon: IssuedCouponModel): Pair<String, String> =
        coupon.startDateTime.toDisplayDate() to coupon.endDateTime.toDisplayDate()

    /**
     * 종료일까지 남은 날 수. 종료 당일이면 0, 이미 지났거나 날짜를 읽을 수 없으면 null.
     */
    fun remainingDays(coupon: IssuedCouponModel, today: LocalDate = LocalDate.now()): Long? {
        val endDate = coupon.endDateTime.toLocalDate() ?: return null
        return ChronoUnit.DAYS.between(today, endDate).takeIf { it >= 0 }
    }

    /** 마이페이지 쿠폰 섹션 개수. 다음 페이지가 있으면 `N+개` 로 표시한다. */
    fun sectionCount(page: IssuedCouponPageModel): SectionCount =
        SectionCount(count = page.coupons.size, hasMore = page.hasMore)

    data class SectionCount(val count: Int, val hasMore: Boolean)

    private fun String.toLocalDate(): LocalDate? = try {
        LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate()
    } catch (e: DateTimeParseException) {
        null
    }

    private fun String.toDisplayDate(): String = toLocalDate()?.format(PERIOD_FORMAT) ?: this
}
