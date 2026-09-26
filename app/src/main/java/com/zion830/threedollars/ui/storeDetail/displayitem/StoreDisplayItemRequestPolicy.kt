package com.zion830.threedollars.ui.storeDetail.displayitem

/**
 * 활동 유도 모달 조회 시점. 상세가 처음 화면에 보였을 때 가게마다 한 번만 조회한다.
 * 정보 수정·신고·리뷰 작성 뒤 돌아와 다시 보여도 재조회하지 않는다(세션 조회수가 부풀지 않게).
 */
object StoreDisplayItemRequestPolicy {
    fun shouldRequest(
        storeId: String,
        requestedStoreId: String?,
        hasContent: Boolean,
        hasLocation: Boolean,
    ): Boolean = storeId.isNotBlank() && hasContent && hasLocation && storeId != requestedStoreId
}
