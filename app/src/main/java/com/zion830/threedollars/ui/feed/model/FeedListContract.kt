package com.zion830.threedollars.ui.feed.model

import androidx.compose.runtime.Immutable
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.domain.community.data.AdvertisementModelV2
import com.threedollar.domain.feed.model.FeedModel
import com.threedollar.domain.feed.model.FeedPageModel

/**
 * 우리 동네 소식 목록 상태. 페이지 반영은 순수 함수로 두어 유닛 테스트한다.
 */
@Immutable
data class FeedListUiState(
    val feeds: List<FeedModel> = emptyList(),
    val nextCursor: String? = null,
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val advertisement: AdvertisementModelV2? = null,
) {
    val canLoadMore: Boolean
        get() = nextCursor != null && !isLoading && !isRefreshing

    /** 첫 조회가 끝났고 피드가 0건이면 빈 화면을 보여준다. */
    val isEmpty: Boolean
        get() = hasLoaded && feeds.isEmpty()

    fun appendPage(page: FeedPageModel): FeedListUiState {
        val loadedIds = feeds.mapTo(HashSet()) { it.feedId }
        return copy(
            feeds = feeds + page.feeds.filterNot { it.feedId in loadedIds },
            nextCursor = page.nextCursor,
            hasLoaded = true,
            isLoading = false,
        )
    }

    /** 당겨서 새로고침은 첫 페이지로 목록을 교체한다. */
    fun replacePage(page: FeedPageModel): FeedListUiState = copy(
        feeds = page.feeds,
        nextCursor = page.nextCursor,
        hasLoaded = true,
        isLoading = false,
        isRefreshing = false,
    )
}

@Immutable
sealed interface FeedListUiIntent {
    data object OnInit : FeedListUiIntent
    data object OnRefresh : FeedListUiIntent
    data object OnLoadNextPage : FeedListUiIntent
    data object OnCloseClick : FeedListUiIntent
    data class OnFeedClick(val feed: FeedModel) : FeedListUiIntent
    data class OnAdvertisementClick(val advertisement: AdvertisementModelV2) : FeedListUiIntent
}

@Immutable
sealed interface FeedListUiEffect {
    data object Close : FeedListUiEffect
    data class OpenLink(val link: SDLink) : FeedListUiEffect
    data class OpenAdvertisement(val advertisement: AdvertisementModelV2) : FeedListUiEffect
    data class ShowErrorAlert(val message: String?) : FeedListUiEffect
}

/**
 * 피드 진입 좌표. 홈이 저장한 지도 좌표를 우선 쓰고, (0, 0) 처럼 확보 전 값이면 null 로 보고 기기 위치로 대신한다.
 */
data class FeedLocation(
    val mapLatitude: Double?,
    val mapLongitude: Double?,
    val deviceLatitude: Double?,
    val deviceLongitude: Double?,
) {
    companion object {
        fun of(map: Pair<Double, Double>?, device: Pair<Double, Double>?): FeedLocation {
            val validMap = map?.takeIf { it.isValid() }
            val validDevice = device?.takeIf { it.isValid() }
            val mapPoint = validMap ?: validDevice
            return FeedLocation(
                mapLatitude = mapPoint?.first,
                mapLongitude = mapPoint?.second,
                deviceLatitude = (validDevice ?: validMap)?.first,
                deviceLongitude = (validDevice ?: validMap)?.second,
            )
        }

        private fun Pair<Double, Double>.isValid() = !(first == 0.0 && second == 0.0)
    }
}
