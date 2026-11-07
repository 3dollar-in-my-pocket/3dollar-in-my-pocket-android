package com.zion830.threedollars.ui.home.data

import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListCardStoreInfoModel
import com.threedollar.common.R as CommonR

/** 홈 리스트 카드 찜 버튼의 상태 계산. ViewModel 밖에서 단위 테스트하려고 순수 함수로 둔다. */
internal object HomeListCardFavorite {

    /** 찜 토글에 성공한 가게의 카드만 [isFavorite] 로 바꾼다. 같은 가게 카드가 여러 장이면 모두 바꾼다. */
    fun withFavorite(
        cards: List<HomeListCardModel>,
        storeId: String,
        isFavorite: Boolean,
    ): List<HomeListCardModel> {
        return cards.map { card ->
            val storeInfo = (card as? HomeListCardModel.BasicCard)?.storeInfo
            if (storeInfo == null || storeInfo.storeId != storeId || storeInfo.isSubscriber == isFavorite) {
                card
            } else {
                card.copy(storeInfo = storeInfo.copy(isSubscriber = isFavorite))
            }
        }
    }

    /** iOS 와 같은 파라미터. `value` 는 탭한 순간의 의도(등록 시도면 on, 해제 시도면 off). */
    fun clickLogParams(storeInfo: HomeListCardStoreInfoModel): Map<ParameterName, String> {
        return buildMap {
            put(ParameterName.STORE_ID, storeInfo.storeId)
            storeInfo.storeType?.let { put(ParameterName.STORE_TYPE, it) }
            put(ParameterName.VALUE, if (storeInfo.isSubscriber) "off" else "on")
        }
    }

    /**
     * 미리보기 시트(가게 상세 VM)의 찜 상태 변화 중 리스트 카드에 반영할 것만 고른다.
     * 다른 가게로 바뀌는 순간은 이전 가게 값이 남아 있을 수 있어 건너뛰고, 같은 가게 안에서 바뀐 값(토글·서버 응답)만 돌려준다.
     */
    fun favoriteChangeOrNull(previous: StoreFavoriteState?, current: StoreFavoriteState): StoreFavoriteState? {
        if (previous == null || current.storeId.isBlank()) return null
        if (previous.storeId != current.storeId || previous.isFavorite == current.isFavorite) return null
        return current
    }

    fun toastMessageRes(isFavorite: Boolean): Int {
        return if (isFavorite) CommonR.string.store_detail_favorite_added else CommonR.string.store_detail_favorite_removed
    }
}

internal data class StoreFavoriteState(val storeId: String, val isFavorite: Boolean)

/** 같은 가게에 찜 요청이 이미 나가 있으면 다음 탭을 막는다. */
internal class HomeListFavoriteRequestGate {
    private val inFlightStoreIds = mutableSetOf<String>()

    fun tryStart(storeId: String): Boolean = inFlightStoreIds.add(storeId)

    fun finish(storeId: String) {
        inFlightStoreIds.remove(storeId)
    }
}
