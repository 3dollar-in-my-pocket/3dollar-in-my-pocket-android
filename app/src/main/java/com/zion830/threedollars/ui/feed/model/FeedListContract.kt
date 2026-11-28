package com.zion830.threedollars.ui.feed.model

import androidx.compose.runtime.Immutable
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.section.home.SDHomeNewsItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsSectionModel

/**
 * 우리 동네 소식 목록 상태. 서버가 내려준 소식 섹션([SDHomeNewsSectionModel])의 헤더와 카드를 서버 순서대로 들고 있는다.
 * 페이지 반영은 순수 함수로 두어 유닛 테스트한다.
 */
@Immutable
data class FeedListUiState(
    val header: SDHeaderModel? = null,
    val items: List<SDHomeNewsItemModel> = emptyList(),
    val nextCursor: String? = null,
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isFailed: Boolean = false,
    val location: FeedLocation? = null,
    val locationRevision: Long = 0L,
    val listRevision: Long? = null,
    val isTabSelected: Boolean = false,
    val newsTabId: String? = null,
    val listNewsTabId: String? = null,
    val hiddenAdCardIds: Set<String> = emptySet(),
    val latestFeedId: String? = null,
    val lastSeenFeedId: String? = null,
) {
    /** 그릴 카드. 끝내 못 불러온 광고 카드는 자리를 접는다. */
    val visibleItems: List<SDHomeNewsItemModel>
        get() = if (hiddenAdCardIds.isEmpty()) items else items.filterNot { it is SDHomeNewsItemModel.AdMob && it.cardId in hiddenAdCardIds }

    val canLoadMore: Boolean
        get() = nextCursor != null && !isLoading && !isFailed

    /** 첫 조회가 끝났고 서버 items 가 비었으면 빈 화면을 보여준다. */
    val isEmpty: Boolean
        get() = hasLoaded && items.isEmpty()

    val showsSkeleton: Boolean
        get() = isLoading && !hasLoaded

    val needsReload: Boolean
        get() = location != null && (listRevision != locationRevision || listNewsTabId != newsTabId || isFailed)

    val showsNewBadge: Boolean
        get() = FeedNewBadgePolicy.isVisible(isTabSelected, latestFeedId, lastSeenFeedId)

    fun changeLocation(location: FeedLocation, revision: Long): FeedListUiState {
        if (revision == locationRevision && location == this.location) return this
        return copy(location = location, locationRevision = revision)
    }

    /** 헤더는 다음 응답이 올 때까지 이전 값을 그대로 둔다. */
    fun startReload(): FeedListUiState = copy(
        items = emptyList(),
        nextCursor = null,
        hasLoaded = false,
        isLoading = true,
        isFailed = false,
        listRevision = locationRevision,
        listNewsTabId = newsTabId,
        hiddenAdCardIds = emptySet(),
    )

    /** 서버 순서를 지키고, 이미 받은 cardId 는 다시 붙이지 않는다. */
    fun appendPage(page: SDHomeNewsSectionModel): FeedListUiState {
        val loadedIds = items.mapTo(HashSet()) { it.cardId }
        val newItems = page.items.filter { loadedIds.add(it.cardId) }
        return copy(
            header = page.header ?: header,
            items = items + newItems,
            nextCursor = page.nextPageCursor,
            hasLoaded = true,
            isLoading = false,
            isFailed = false,
        )
    }

    fun failPage(): FeedListUiState = if (hasLoaded) {
        copy(isLoading = false)
    } else {
        copy(isLoading = false, isFailed = true)
    }

    fun hideAdCard(cardId: String): FeedListUiState = copy(hiddenAdCardIds = hiddenAdCardIds + cardId)

    fun contentCard(cardId: String): SDHomeNewsItemModel.ContentCard? =
        items.firstOrNull { it.cardId == cardId } as? SDHomeNewsItemModel.ContentCard

    fun adMobCard(cardId: String): SDHomeNewsItemModel.AdMob? =
        items.firstOrNull { it.cardId == cardId } as? SDHomeNewsItemModel.AdMob

    fun applyLatestFeedId(feedId: String?): FeedListUiState = copy(latestFeedId = feedId).markSeenIfSelected()

    fun selectTab(selected: Boolean, newsTabId: String? = this.newsTabId): FeedListUiState =
        copy(isTabSelected = selected, newsTabId = newsTabId ?: this.newsTabId).markSeenIfSelected()

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
    data class OnTabSelectionChanged(val selected: Boolean, val newsTabId: String? = null) : FeedListUiIntent
    data object OnLoadNextPage : FeedListUiIntent
    data object OnRetry : FeedListUiIntent
    data class OnCardVisible(val cardId: String) : FeedListUiIntent
    data class OnContentCardClick(val cardId: String) : FeedListUiIntent
    data class OnImageClick(val cardId: String, val blockIndex: Int, val imageIndex: Int) : FeedListUiIntent
    data class OnAdMobClick(val cardId: String) : FeedListUiIntent
    data class OnAdMobLoadFailed(val cardId: String) : FeedListUiIntent
}

@Immutable
sealed interface FeedListUiEffect {
    data class OpenLink(val link: SDLink) : FeedListUiEffect
    data class ShowImages(val imageUrls: List<String>, val startIndex: Int) : FeedListUiEffect
}

/** 본문 사진 크게 보기 요청. 누른 블록의 사진 전체를 누른 사진부터 연다. */
object FeedImageViewerRequest {
    fun of(card: SDHomeNewsItemModel.ContentCard, blockIndex: Int, imageIndex: Int): FeedListUiEffect.ShowImages? {
        val urls = card.bodyBlocks.getOrNull(blockIndex)?.images?.mapNotNull { it.url?.takeIf(String::isNotBlank) }
        if (urls.isNullOrEmpty() || imageIndex !in urls.indices) return null
        return FeedListUiEffect.ShowImages(imageUrls = urls, startIndex = imageIndex)
    }
}

/**
 * 섹션 노출 로그는 동네 소식 탭이 보일 때만 보낸다. 응답 전에 탭을 떠나면 보류했다가 돌아올 때 한 번 보내고,
 * 그 사이 목록을 새로 불러오게 되면 보류분은 버리고 새 응답 것을 보낸다.
 */
class FeedSectionImpressionGate<T : Any> {
    private var pending: T? = null

    fun reset() {
        pending = null
    }

    fun onFirstPage(log: T?, isTabSelected: Boolean): T? {
        if (isTabSelected) {
            pending = null
            return log
        }
        pending = log
        return null
    }

    fun onTabSelected(willReload: Boolean): T? {
        val log = pending
        pending = null
        return log.takeUnless { willReload }
    }
}

/** 카드 노출 로그는 목록을 새로 불러올 때마다 카드당 한 번만 보낸다. */
class FeedCardImpressionDedup {
    private val sentCardIds = mutableSetOf<String>()

    fun shouldSend(cardId: String): Boolean = sentCardIds.add(cardId)

    fun reset() {
        sentCardIds.clear()
    }
}

/**
 * 피드 진입 좌표. 홈이 저장한 지도 좌표를 우선 쓰고, (0, 0) 처럼 확보 전 값이면 null 로 보고 기기 위치로 대신한다.
 */
data class FeedLocation(
    val mapLatitude: Double?,
    val mapLongitude: Double?,
    val deviceLatitude: Double?,
    val deviceLongitude: Double?,
    val maxDistanceM: Double? = null,
) {
    companion object {
        fun of(
            map: Pair<Double, Double>?,
            device: Pair<Double, Double>?,
            maxDistanceM: Double? = null,
        ): FeedLocation {
            val validMap = map?.takeIf { it.isValid() }
            val validDevice = device?.takeIf { it.isValid() }
            val mapPoint = validMap ?: validDevice
            return FeedLocation(
                mapLatitude = mapPoint?.first,
                mapLongitude = mapPoint?.second,
                deviceLatitude = (validDevice ?: validMap)?.first,
                deviceLongitude = (validDevice ?: validMap)?.second,
                maxDistanceM = maxDistanceM,
            )
        }

        private fun Pair<Double, Double>.isValid() = !(first == 0.0 && second == 0.0)
    }
}
