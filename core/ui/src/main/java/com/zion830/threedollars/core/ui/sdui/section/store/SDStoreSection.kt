package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import base.compose.ColorWhite
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAppearanceDaySectionModel
import com.threedollar.common.sdui.model.section.SDStoreCalloutSectionModel
import com.threedollar.common.sdui.model.section.SDStoreCouponSectionModel
import com.threedollar.common.sdui.model.section.SDStoreCtaSectionModel
import com.threedollar.common.sdui.model.section.SDStoreEditSectionModel
import com.threedollar.common.sdui.model.section.SDStoreImageSectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV1SectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV2SectionModel
import com.threedollar.common.sdui.model.section.SDStoreMarginSectionModel
import com.threedollar.common.sdui.model.section.SDStorePostSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreReviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreTabSectionModel
import com.threedollar.common.sdui.model.section.SDStoreVisitSectionModel
import com.threedollar.common.sdui.model.section.SDUnknownSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface
import com.zion830.threedollars.core.ui.sdui.section.SDRelatedStoresSection

/**
 * 앱 모듈에만 있는 SDK(지도·광고)를 쓰는 섹션의 내용물.
 */
@Immutable
data class SDStoreSectionSlots(
    val mapContent: @Composable (latitude: Double, longitude: Double, modifier: Modifier) -> Unit = { _, _, _ -> },
    val adContent: @Composable (card: SDStoreAdmobSectionModel.Card, modifier: Modifier) -> Unit = { _, _ -> },
)

/**
 * 가게 상세 섹션 하나를 타입에 맞는 컴포저블로 그린다. 모르는 타입은 그리지 않는다.
 *
 * @param selectedTabIndex TAB 섹션의 선택 탭(스크롤 위치로 정해진다)
 * @param previewActionBarsModifier PREVIEW 섹션 액션 버튼 줄에 붙일 Modifier(하단 칩 바 노출 판단용)
 */
@Composable
fun SDStoreSection(
    section: SDSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    onImageClick: (images: List<SDImageModel>, index: Int) -> Unit = { _, _ -> },
    selectedTabIndex: Int = 0,
    previewActionBarsModifier: Modifier = Modifier,
    slots: SDStoreSectionSlots = SDStoreSectionSlots(),
) {
    when (section) {
        is SDStorePreviewSectionModel -> SDStorePreviewSection(
            model = section,
            onAction = onAction,
            modifier = modifier,
            actionBarsModifier = previewActionBarsModifier,
            onImageClick = onImageClick,
        )
        is SDStoreTabSectionModel -> SDStoreTabSection(
            model = section,
            selectedIndex = selectedTabIndex,
            onTabClick = { _, event -> onAction(event) },
            modifier = modifier,
        )
        is SDStoreEditSectionModel -> SDStoreEditSection(section, onAction, modifier, slots.mapContent)
        is SDStoreCouponSectionModel -> SDStoreCouponSection(section, onAction, modifier)
        is SDStoreVisitSectionModel -> SDStoreVisitSection(section, onAction, modifier)
        is SDStorePostSectionModel -> SDStorePostSection(section, onAction, modifier, onImageClick)
        is SDStoreImageSectionModel -> SDStoreImageSection(section, onAction, modifier)
        is SDStoreAppearanceDaySectionModel -> SDStoreAppearanceDaySection(section, onAction, modifier)
        is SDStoreInfoV1SectionModel -> SDStoreInfoV1Section(section, onAction, modifier)
        is SDStoreInfoV2SectionModel -> SDStoreInfoV2Section(section, onAction, modifier, onImageClick)
        is SDStoreCtaSectionModel -> SDStoreCtaSection(section, onAction, modifier)
        is SDStoreCalloutSectionModel -> SDStoreCalloutSection(section, onAction, modifier)
        is SDStoreReviewSectionModel -> SDStoreReviewSection(section, onAction, modifier, onImageClick)
        is SDStoreAdmobSectionModel -> SDStoreAdmobSection(section, modifier, slots.adContent)
        is SDStoreMarginSectionModel -> SDStoreMarginSection(section, modifier)
        is SDRelatedStoresSectionModel -> SDRelatedStoresSection(
            model = section,
            onCardPressed = { card -> onAction(SDActionEvent(link = card.link, clickLog = card.clickLog)) },
            modifier = modifier
                .fillMaxWidth()
                .sdSurface(style = section.style, defaultBackground = ColorWhite)
                .padding(vertical = SDSectionDefaults.VerticalPadding),
        )
        is SDUnknownSectionModel -> Unit
    }
}
