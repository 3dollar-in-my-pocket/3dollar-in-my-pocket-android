package com.zion830.threedollars.core.ui.sdui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.AppTheme
import base.compose.Gray30
import base.compose.Gray50
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDActionBarRow
import com.zion830.threedollars.core.ui.sdui.component.SDChipRow
import com.zion830.threedollars.core.ui.sdui.component.SDHeader
import com.zion830.threedollars.core.ui.sdui.component.card.SDImagePreviewCard
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDRatingChip
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.element.SDToggleButton
import com.zion830.threedollars.core.ui.sdui.section.SDRelatedStoresSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreAdmobSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreAppearanceDaySection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreCalloutSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreCouponSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreCtaSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreEditSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreImageSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreInfoV1Section
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreInfoV2Section
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreMarginSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePostSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewSectionSlots
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreReviewSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSectionSlots
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSkeletonSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreTabSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreVisitSection

private const val ELEMENT = "element"
private const val COMPONENT = "component"
private const val SECTION = "section"
private const val SCREEN = "screen"

private val previewSlots = SDStoreSectionSlots(
    mapContent = { _, _, modifier -> PlaceholderBox(label = "지도", modifier = modifier) },
    adContent = { _, modifier ->
        PlaceholderBox(label = "AdMob", modifier = modifier.fillMaxWidth().height(100.dp))
    }
)

@Composable
private fun PlaceholderBox(label: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Gray30), contentAlignment = Alignment.Center) {
        Text(text = label, color = Gray50)
    }
}

@Composable
private fun SDPreviewContainer(content: @Composable () -> Unit) {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun SDStoreScreenPreview(sections: List<SDSectionModel>) {
    AppTheme {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            sections.forEach { section ->
                SDStoreSection(section = section, onAction = {}, slots = previewSlots)
            }
        }
    }
}

@Preview(name = "SDText - plain / html", group = ELEMENT, showBackground = true, widthDp = 360)
@Composable
private fun SDTextPreview() {
    SDPreviewContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SDText(model = SDPreviewFixtures.plainText)
            SDText(model = SDPreviewFixtures.htmlText)
        }
    }
}

@Preview(name = "SDChip", group = ELEMENT, showBackground = true, widthDp = 360)
@Composable
private fun SDChipPreview() {
    SDPreviewContainer {
        SDChip(model = SDPreviewFixtures.starChip)
    }
}

@Preview(name = "SDButton", group = ELEMENT, showBackground = true, widthDp = 360)
@Composable
private fun SDButtonPreview() {
    SDPreviewContainer {
        SDButton(model = SDPreviewFixtures.visitButton, onAction = {})
    }
}

@Preview(name = "SDToggleButton - unselected / selected", group = ELEMENT, showBackground = true, widthDp = 360)
@Composable
private fun SDToggleButtonPreview() {
    SDPreviewContainer {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SDToggleButton(model = SDPreviewFixtures.postLike, onAction = {})
            SDToggleButton(model = SDPreviewFixtures.postLikeSelected, onAction = {})
        }
    }
}

@Preview(name = "SDRatingChip", group = ELEMENT, showBackground = true, widthDp = 360)
@Composable
private fun SDRatingChipPreview() {
    SDPreviewContainer {
        SDRatingChip(model = SDPreviewFixtures.ratingChip)
    }
}

@Preview(name = "SDChipRow", group = COMPONENT, showBackground = true, widthDp = 360)
@Composable
private fun SDChipRowPreview() {
    SDPreviewContainer {
        SDChipRow(chips = SDPreviewFixtures.metadataChips)
    }
}

@Preview(name = "SDHeader - subtitle + trailing action", group = COMPONENT, showBackground = true, widthDp = 360)
@Composable
private fun SDHeaderPreview() {
    SDPreviewContainer {
        SDHeader(model = SDPreviewFixtures.header, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(name = "SDActionBarRow", group = COMPONENT, showBackground = true, widthDp = 360)
@Composable
private fun SDActionBarRowPreview() {
    AppTheme {
        SDActionBarRow(actionBars = SDPreviewFixtures.actionBars, onAction = {}, modifier = Modifier.padding(vertical = 16.dp))
    }
}

@Preview(name = "SDImagePreviewCard", group = COMPONENT, showBackground = true, widthDp = 360)
@Composable
private fun SDImagePreviewCardPreview() {
    SDPreviewContainer {
        SDImagePreviewCard(model = SDPreviewFixtures.imagePreviewCard, onPressed = {})
    }
}

@Preview(name = "CALLOUT", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreCalloutSectionPreview() {
    AppTheme {
        SDStoreCalloutSection(model = SDPreviewFixtures.callout, onAction = {})
    }
}

@Preview(name = "PREVIEW - 사장님 가게", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStorePreviewSectionPreview() {
    AppTheme {
        SDStorePreviewSection(model = SDPreviewFixtures.preview, onAction = {})
    }
}

@Preview(name = "PREVIEW - 유저 제보 가게", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStorePreviewSectionUserStorePreview() {
    AppTheme {
        SDStorePreviewSection(model = SDPreviewFixtures.userStorePreview, onAction = {})
    }
}

@Preview(name = "PREVIEW - 홈 미리보기 시트(tip)", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStorePreviewSectionTipPreview() {
    AppTheme {
        SDStorePreviewSection(
            model = SDPreviewFixtures.userStorePreview,
            onAction = {},
            slots = SDStorePreviewSectionSlots(
                headerTrailing = {
                    Box(
                        modifier = Modifier
                            .size(width = 68.dp, height = 32.dp)
                            .background(Gray30)
                    )
                },
                headerTrailingWidth = 72.dp,
                bodiesAsCarousel = true,
            ),
        )
    }
}

@Preview(name = "TAB", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreTabSectionPreview() {
    AppTheme {
        SDStoreTabSection(model = SDPreviewFixtures.tab, selectedIndex = 1, onTabClick = { _, _ -> })
    }
}

@Preview(name = "EDIT", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreEditSectionPreview() {
    AppTheme {
        SDStoreEditSection(model = SDPreviewFixtures.edit, onAction = {}, mapContent = previewSlots.mapContent)
    }
}

@Preview(name = "COUPON", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreCouponSectionPreview() {
    AppTheme {
        SDStoreCouponSection(model = SDPreviewFixtures.coupon, onAction = {})
    }
}

@Preview(name = "VISIT", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreVisitSectionPreview() {
    AppTheme {
        SDStoreVisitSection(model = SDPreviewFixtures.visit, onAction = {})
    }
}

@Preview(name = "VISIT - empty", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreVisitSectionEmptyPreview() {
    AppTheme {
        SDStoreVisitSection(model = SDPreviewFixtures.visitEmpty, onAction = {})
    }
}

@Preview(name = "POST", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStorePostSectionPreview() {
    AppTheme {
        SDStorePostSection(model = SDPreviewFixtures.post, onAction = {})
    }
}

@Preview(name = "IMAGE", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreImageSectionPreview() {
    AppTheme {
        SDStoreImageSection(model = SDPreviewFixtures.imageSection, onAction = {})
    }
}

@Preview(name = "IMAGE - empty", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreImageSectionEmptyPreview() {
    AppTheme {
        SDStoreImageSection(model = SDPreviewFixtures.imageSectionEmpty, onAction = {})
    }
}

@Preview(name = "APPEARANCE_DAY", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreAppearanceDaySectionPreview() {
    AppTheme {
        SDStoreAppearanceDaySection(model = SDPreviewFixtures.appearanceDay, onAction = {})
    }
}

@Preview(name = "INFO_V1 - 메뉴 더보기", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreInfoV1SectionPreview() {
    AppTheme {
        SDStoreInfoV1Section(model = SDPreviewFixtures.infoV1, onAction = {})
    }
}

@Preview(name = "INFO_V2", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreInfoV2SectionPreview() {
    AppTheme {
        SDStoreInfoV2Section(model = SDPreviewFixtures.infoV2, onAction = {})
    }
}

@Preview(name = "CTA", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreCtaSectionPreview() {
    AppTheme {
        SDStoreCtaSection(model = SDPreviewFixtures.cta, onAction = {})
    }
}

@Preview(name = "REVIEW - 일반 / 블라인드 / 사장님 답글", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreReviewSectionPreview() {
    AppTheme {
        SDStoreReviewSection(model = SDPreviewFixtures.review, onAction = {})
    }
}

@Preview(name = "REVIEW - blinded only", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreReviewSectionBlindedPreview() {
    AppTheme {
        SDStoreReviewSection(model = SDPreviewFixtures.blindedReview, onAction = {})
    }
}

@Preview(name = "AD_MOB", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreAdmobSectionPreview() {
    AppTheme {
        SDStoreAdmobSection(model = SDPreviewFixtures.admob, adContent = previewSlots.adContent)
    }
}

@Preview(name = "MARGIN", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreMarginSectionPreview() {
    AppTheme {
        SDStoreMarginSection(model = SDPreviewFixtures.margin)
    }
}

@Preview(name = "RELATED_STORES", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDRelatedStoresSectionPreview() {
    AppTheme {
        SDRelatedStoresSection(model = SDPreviewFixtures.relatedStores, onCardPressed = {})
    }
}

@Preview(name = "SKELETON", group = SECTION, showBackground = true, widthDp = 360)
@Composable
private fun SDStoreSkeletonSectionPreview() {
    AppTheme {
        SDStoreSkeletonSection()
    }
}

@Preview(name = "사장님 가게 전체 화면", group = SCREEN, showBackground = true, widthDp = 360, heightDp = 2400)
@Composable
private fun SDBossStoreScreenPreview() {
    SDStoreScreenPreview(sections = SDPreviewFixtures.bossStoreScreen)
}

@Preview(name = "유저 제보 가게 전체 화면", group = SCREEN, showBackground = true, widthDp = 360, heightDp = 2400)
@Composable
private fun SDUserStoreScreenPreview() {
    SDStoreScreenPreview(sections = SDPreviewFixtures.userStoreScreen)
}
