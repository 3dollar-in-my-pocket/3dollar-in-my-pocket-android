package com.zion830.threedollars.core.ui.sdui.section.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCategoryFilterModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDHomeBottomSheetTabs

/** 홈 시트 표시 뷰. 선택·통신 상태·핸들·주변 목록은 호출부가 소유한다. */
@Composable
fun SDHomeCurationView(
    tabs: SDHomeBottomSheetTabsModel,
    selectedTabId: String?,
    section: SDHomeCurationSectionModel,
    selectedCategoryIds: Map<String, String>,
    onTabClick: (SDHomeBottomSheetTabModel) -> Unit,
    onCategoryClick: (SDHomeCurationItemModel.Carousel, SDHomeCurationCategoryFilterModel) -> Unit,
    onCardClick: (ImagePreviewCardModel) -> Unit,
    modifier: Modifier = Modifier,
    cardsByCarousel: Map<String, SDHomeCurationCardsModel> = emptyMap(),
    listState: LazyListState = rememberLazyListState(),
    carouselListStates: Map<String, LazyListState> = emptyMap(),
    nearbyContent: @Composable () -> Unit = {},
    adMobContent: (@Composable (SDAdMobCardModel) -> Unit)? = null,
    adMobCardWidth: Dp = SDHomeCurationDefaults.CardSize,
    hiddenAdCardIds: Set<String> = emptySet(),
    cardImageContentScale: (ImagePreviewCardModel) -> ContentScale = { ContentScale.Crop },
    emptyContent: @Composable () -> Unit = {},
    sectionStateContent: @Composable (() -> Unit)? = null,
    carouselStateContent: @Composable (SDHomeCurationItemModel.Carousel) -> Unit = {},
    onHeaderAction: (SDActionEvent) -> Unit = {},
) {
    val supportedTabs = tabs.tabs.filter { it.viewType == "CURATION" || it.viewType == "STORE_LIST" }
    val selectedTab = supportedTabs.firstOrNull { it.tabId == selectedTabId }
    Column(modifier = modifier.background(ColorWhite)) {
        if (supportedTabs.isNotEmpty()) {
            SDHomeBottomSheetTabs(
                model = SDHomeBottomSheetTabsModel(supportedTabs),
                selectedTabId = selectedTab?.tabId,
                onTabClick = onTabClick,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
            )
        }
        if (selectedTab?.viewType != "CURATION") {
            nearbyContent()
        } else if (sectionStateContent != null) {
            sectionStateContent()
        } else {
            val visibleItems = section.items.filterNot {
                it is SDHomeCurationItemModel.Unknown ||
                    (it is SDHomeCurationItemModel.AdMob && (adMobContent == null || it.card.cardId in hiddenAdCardIds))
            }
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                if (visibleItems.isEmpty()) item { emptyContent() }
                itemsIndexed(
                    visibleItems,
                    key = { index, item ->
                        when (item) {
                            is SDHomeCurationItemModel.Carousel -> "carousel:${item.carouselId}"
                            is SDHomeCurationItemModel.AdMob -> "ad:${item.card.cardId}"
                            is SDHomeCurationItemModel.Unknown -> "unknown:$index"
                        }
                    },
                    contentType = { _, item ->
                        when (item) {
                            is SDHomeCurationItemModel.Carousel -> "CAROUSEL"
                            is SDHomeCurationItemModel.AdMob -> "ADMOB_CARD"
                            is SDHomeCurationItemModel.Unknown -> item.rawType
                        }
                    },
                ) { _, item ->
                    when (item) {
                        is SDHomeCurationItemModel.Carousel -> SDHomeCurationCarousel(
                            model = item,
                            cards = cardsByCarousel[item.carouselId]?.cards ?: item.cards,
                            selectedCategoryId = selectedCategoryIds[item.carouselId] ?: item.defaultCategoryId,
                            onCategoryClick = { onCategoryClick(item, it) },
                            onCardClick = onCardClick,
                            listState = carouselListStates[item.carouselId] ?: rememberLazyListState(),
                            onHeaderAction = onHeaderAction,
                            adMobContent = adMobContent,
                            adMobCardWidth = adMobCardWidth,
                            hiddenAdCardIds = hiddenAdCardIds,
                            cardImageContentScale = cardImageContentScale,
                            emptyContent = emptyContent,
                            stateContent = { carouselStateContent(item) },
                        )
                        is SDHomeCurationItemModel.AdMob -> adMobContent?.let { adContent ->
                            Box(modifier = Modifier.fillMaxWidth().height(item.card.height.coerceAtLeast(0).dp)) {
                                adContent(item.card)
                            }
                        }
                        is SDHomeCurationItemModel.Unknown -> Unit
                    }
                }
            }
        }
    }
}
