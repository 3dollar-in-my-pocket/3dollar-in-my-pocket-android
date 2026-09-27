package com.zion830.threedollars.ui.storeDetail.post.model

/** `postList?storeId=` 딥링크 검증. 숫자가 아닌 storeId 는 열지 않고 기본 랜딩으로 보낸다. */
object StorePostListDeepLink {
    fun validStoreId(storeId: String?): String? = storeId?.trim()?.takeIf { it.toLongOrNull() != null }
}
