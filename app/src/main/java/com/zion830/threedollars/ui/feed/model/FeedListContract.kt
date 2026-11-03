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
    val isFailed: Boolean = false,
    val advertisement: AdvertisementModelV2? = null,
    val location: FeedLocation? = null,
    val locationRevision: Long = 0L,
    val listRevision: Long? = null,
    val isTabSelected: Boolean = false,
    val latestFeedId: String? = null,
    val lastSeenFeedId: String? = null,
) {
    val canLoadMore: Boolean
        get() = nextCursor != null && !isLoading && !isFailed

    /** 첫 조회가 끝났고 피드가 0건이면 빈 화면을 보여준다. */
    val isEmpty: Boolean
        get() = hasLoaded && feeds.isEmpty()

    val showsSkeleton: Boolean
        get() = isLoading && !hasLoaded

    val needsReload: Boolean
        get() = location != null && (listRevision != locationRevision || isFailed)

    val showsNewBadge: Boolean
        get() = FeedNewBadgePolicy.isVisible(isTabSelected, latestFeedId, lastSeenFeedId)

    fun changeLocation(location: FeedLocation, revision: Long): FeedListUiState {
        if (revision == locationRevision && location == this.location) return this
        return copy(location = location, locationRevision = revision)
    }

    fun startReload(): FeedListUiState = copy(
        feeds = emptyList(),
        nextCursor = null,
        hasLoaded = false,
        isLoading = true,
        isFailed = false,
        listRevision = locationRevision,
    )

    fun appendPage(page: FeedPageModel): FeedListUiState {
        val isFirstPage = !hasLoaded
        val loadedIds = feeds.mapTo(HashSet()) { it.feedId }
        val appended = copy(
            feeds = feeds + page.feeds.filterNot { it.feedId in loadedIds },
            nextCursor = page.nextCursor,
            hasLoaded = true,
            isLoading = false,
            isFailed = false,
        )
        return if (isFirstPage) appended.applyLatestFeedId(page.feeds.firstOrNull()?.feedId) else appended
    }

    fun failPage(): FeedListUiState = if (hasLoaded) {
        copy(isLoading = false)
    } else {
        copy(isLoading = false, isFailed = true)
    }

    fun applyLatestFeedId(feedId: String?): FeedListUiState = copy(latestFeedId = feedId).markSeenIfSelected()

    fun selectTab(selected: Boolean): FeedListUiState = copy(isTabSelected = selected).markSeenIfSelected()

    private fun markSeenIfSelected(): FeedListUiState {
        if (!isTabSelected) return this
        val latest = latestFeedId ?: return this
        if (!FeedNewBadgePolicy.isNewer(latest, lastSeenFeedId)) return this
        return copy(lastSeenFeedId = latest)
    }
}

object FeedNewBadgePolicy {
    fun isVisible(isTabSelected: Boolean, latestFeedId: String?, lastSeenFeedId: String?): Boolean {
        if (isTabSelected) return false
        val latest = latestFeedId ?: return false
        return isNewer(latest, lastSeenFeedId)
    }

    fun isNewer(feedId: String, lastSeenFeedId: String?): Boolean = lastSeenFeedId == null || feedId > lastSeenFeedId
}

@Immutable
sealed interface FeedListUiIntent {
    data class OnLocationChanged(val location: FeedLocation, val revision: Long) : FeedListUiIntent
    data class OnTabSelectionChanged(val selected: Boolean) : FeedListUiIntent
    data object OnLoadNextPage : FeedListUiIntent
    data object OnRetry : FeedListUiIntent
    data class OnFeedClick(val feed: FeedModel) : FeedListUiIntent
    data class OnAdvertisementClick(val advertisement: AdvertisementModelV2) : FeedListUiIntent
}

@Immutable
sealed interface FeedListUiEffect {
    data class OpenLink(val link: SDLink) : FeedListUiEffect
    data class OpenAdvertisement(val advertisement: AdvertisementModelV2) : FeedListUiEffect
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
