package com.zion830.threedollars.core.ui.sdui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.AppTheme
import base.compose.ColorWhite
import base.compose.Gray20
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDHomeBottomSheetTabs
import com.zion830.threedollars.core.ui.sdui.component.SDHomeCurationCategoryChip
import com.zion830.threedollars.core.ui.sdui.component.card.SDImagePreviewCard
import com.zion830.threedollars.core.ui.sdui.section.home.SDHomeCurationCarousel
import com.zion830.threedollars.core.ui.sdui.section.home.SDHomeCurationView

@Preview(name = "Category selected", group = "home-curation/element", showBackground = true)
@Composable
private fun CategoryPreview() {
    val carousel = HomeCurationPreviewFixtures.section.items.first() as SDHomeCurationItemModel.Carousel
    AppTheme { SDHomeCurationCategoryChip(carousel.categoryFilters.first(), selected = true, onCategoryClick = {}) }
}

@Preview(name = "100dp preview card", group = "home-curation/component", showBackground = true)
@Composable
private fun CardPreview() {
    val card = HomeCurationPreviewFixtures.cardsByCarousel.values.first().cards.first() as ImagePreviewCardModel
    AppTheme { SDImagePreviewCard(card, modifier = Modifier.width(100.dp), imageSize = 100.dp, onPressed = {}) }
}

@Preview(name = "Tabs", group = "home-curation/component", showBackground = true, widthDp = 361)
@Composable
private fun TabsPreview() {
    var selected by remember { mutableStateOf("CURATION") }
    AppTheme { SDHomeBottomSheetTabs(HomeCurationPreviewFixtures.tabs, selected, { selected = it.tabId }) }
}

@Preview(name = "Carousel", group = "home-curation/section", showBackground = true, widthDp = 393)
@Composable
private fun CarouselPreview() {
    val carousel = HomeCurationPreviewFixtures.section.items.first() as SDHomeCurationItemModel.Carousel
    var selected by remember { mutableStateOf(carousel.defaultCategoryId) }
    AppTheme {
        SDHomeCurationCarousel(
            carousel, selected, { selected = it.categoryId }, {},
            cardImageContentScale = { card ->
                if ((card.image?.style?.width ?: 100f) < 100f) ContentScale.Fit else ContentScale.Crop
            },
        )
    }
}

@Preview(name = "Curation sheet", group = "home-curation/screen", showBackground = true, widthDp = 393, heightDp = 688)
@Preview(name = "Small width", group = "home-curation/screen", showBackground = true, widthDp = 320, heightDp = 640)
@Composable
private fun SheetPreview() = CurationSheetPreview()

@Preview(name = "Empty input", group = "home-curation/screen", showBackground = true, widthDp = 393, heightDp = 688)
@Composable
private fun EmptyPreview() = CurationSheetPreview(SDHomeCurationSectionModel())

@Composable
private fun CurationSheetPreview(section: SDHomeCurationSectionModel = HomeCurationPreviewFixtures.section) {
    var selectedTab by remember { mutableStateOf("CURATION") }
    var categories by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    AppTheme {
        Column(Modifier.fillMaxSize().background(ColorWhite)) {
            Box(Modifier.fillMaxWidth().height(12.dp), contentAlignment = Alignment.BottomCenter) {
                Box(Modifier.size(40.dp, 5.dp).background(Gray20, RoundedCornerShape(1000.dp)))
            }
            SDHomeCurationView(
                tabs = HomeCurationPreviewFixtures.tabs,
                selectedTabId = selectedTab,
                section = section,
                selectedCategoryIds = categories,
                onTabClick = { selectedTab = it.tabId },
                onCategoryClick = { carousel, category -> categories = categories + (carousel.carouselId to category.categoryId) },
                onCardClick = {},
                modifier = Modifier.fillMaxSize(),
                cardImageContentScale = { card ->
                    if ((card.image?.style?.width ?: 100f) < 100f) ContentScale.Fit else ContentScale.Crop
                },
            )
        }
    }
}
