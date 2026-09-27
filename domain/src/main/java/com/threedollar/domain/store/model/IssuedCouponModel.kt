package com.threedollar.domain.store.model

/** 내가 발급받은 쿠폰 한 장. [status]는 서버 `issued.status` 이고 알 수 없는 값은 [IssuedCouponStatus.EXPIRED] 로 본다. */
data class IssuedCouponModel(
    val issuedKey: String,
    val name: String,
    val startDateTime: String,
    val endDateTime: String,
    val status: IssuedCouponStatus,
    val storeId: String,
    val storeName: String,
    val storeCategoryName: String,
    val storeCategoryImageUrl: String?,
)

enum class IssuedCouponStatus {
    ISSUED,
    USED,
    EXPIRED;

    companion object {
        fun from(value: String?): IssuedCouponStatus = entries.firstOrNull { it.name == value } ?: EXPIRED
    }
}

/** 내 쿠폰 목록 한 페이지. [nextCursor]가 null 이면 마지막 페이지다. */
data class IssuedCouponPageModel(
    val coupons: List<IssuedCouponModel>,
    val nextCursor: String?,
) {
    val hasMore: Boolean
        get() = nextCursor != null
}
