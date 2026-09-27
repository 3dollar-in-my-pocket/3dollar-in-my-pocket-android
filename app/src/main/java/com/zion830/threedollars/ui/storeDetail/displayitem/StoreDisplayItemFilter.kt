package com.zion830.threedollars.ui.storeDetail.displayitem

import com.threedollar.domain.store.model.StoreDisplayItemModel
import com.threedollar.domain.store.model.StoreDisplayItemType

/**
 * 가게 상세 활동 유도 모달 후보 중 이번 진입에 보여줄 것만 고른다.
 * 서버가 보이게 한 항목 중 앱이 아는 타입이고, 이번이 이 가게의 [viewCount]번째 조회일 때 세션 조건을 만족해야 한다.
 */
object StoreDisplayItemFilter {
    fun eligible(items: List<StoreDisplayItemModel>, viewCount: Int): List<StoreDisplayItemModel> =
        items.filter { item ->
            item.isVisible &&
                item.itemType != StoreDisplayItemType.UNKNOWN &&
                (item.trigger?.conditions?.sessionViewCountRange?.contains(viewCount) ?: true)
        }
}
