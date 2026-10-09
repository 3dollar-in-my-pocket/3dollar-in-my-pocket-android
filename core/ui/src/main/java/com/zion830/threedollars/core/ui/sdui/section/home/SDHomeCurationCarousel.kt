package com.zion830.threedollars.core.ui.sdui.section.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.dpToSp
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCategoryFilterModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.zion830.threedollars.core.ui.sdui.component.SDHeader
import com.zion830.threedollars.core.ui.sdui.component.SDHomeCurationCategoryChip
import com.zion830.threedollars.core.ui.sdui.component.card.SDImagePreviewCard

object SDHomeCurationDefaults {
    val CardSize = 100.dp
    val HorizontalPadding = 20.dp
    val CardSpacing = 8.dp
    val SectionPadding = 20.dp
    val CategorySpacing = 6.dp
}

/** 조회·로그·광고 SDK는 호출부에 두고 원본 모델을 콜백과 슬롯에 전달한다. */
@Composable
fun SDHomeCurationCarousel(
    model: SDHomeCurationItemModel.Carousel,
    selectedCategoryId: String?,
    onCategoryClick: (SDHomeCurationCategoryFilterModel) -> Unit,
    onCardClick: (ImagePreviewCardModel) -> Unit,
    modifier: Modifier = Modifier,
    cards: List<SDCardModel> = model.cards,
    listState: LazyListState = rememberLazyListState(),
    onHeaderAction: (SDActionEvent) -> Unit = {},
    adMobContent: (@Composable (SDAdMobCardModel) -> Unit)? = null,
    adMobCardWidth: Dp = SDHomeCurationDefaults.CardSize,
    hiddenAdCardIds: Set<String> = emptySet(),
    cardImageContentScale: (ImagePreviewCardModel) -> ContentScale = { ContentScale.Crop },
    emptyContent: @Composable () -> Unit = {},
    stateContent: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth().background(ColorWhite).drawBehind {
            val stroke = 1.dp.toPx()
            drawLine(Gray10, Offset(0f, size.height - stroke / 2), Offset(size.width, size.height - stroke / 2), strokeWidth = stroke)
        }.padding(vertical = SDHomeCurationDefaults.SectionPadding),
    ) {
        model.header?.let { header ->
            SDHeader(
                model = header,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = SDHomeCurationDefaults.HorizontalPadding)
                    .semantics { heading() },
                onAction = onHeaderAction,
                titleLineHeight = dpToSp(28),
            )
        }
        if (model.categoryFilters.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().selectableGroup(),
                contentPadding = PaddingValues(horizontal = SDHomeCurationDefaults.HorizontalPadding, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(SDHomeCurationDefaults.CategorySpacing),
            ) {
                items(model.categoryFilters, key = { it.categoryId }) { category ->
                    SDHomeCurationCategoryChip(category, category.categoryId == selectedCategoryId, onCategoryClick)
                }
            }
        }
        stateContent()
        val visibleCards = cards.filter {
            it is ImagePreviewCardModel || (it is SDAdMobCardModel && adMobContent != null && it.cardId !in hiddenAdCardIds)
        }
        if (visibleCards.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = SDHomeCurationDefaults.HorizontalPadding)) {
                emptyContent()
            }
        } else {
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(start = SDHomeCurationDefaults.HorizontalPadding, end = SDHomeCurationDefaults.HorizontalPadding, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(SDHomeCurationDefaults.CardSpacing),
            ) {
                items(visibleCards, key = { it.cardId }, contentType = { it.type }) { card ->
                    when (card) {
                        is ImagePreviewCardModel -> SDImagePreviewCard(
                            model = card,
                            modifier = Modifier.width(SDHomeCurationDefaults.CardSize).clipToBounds(),
                            imageSize = SDHomeCurationDefaults.CardSize,
                            imageContentScale = cardImageContentScale(card),
                            onPressed = onCardClick,
                        )
                        is SDAdMobCardModel -> adMobContent?.let { adContent ->
                            Box(modifier = Modifier.width(adMobCardWidth).height(card.height.coerceAtLeast(0).dp)) {
                                adContent(card)
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
