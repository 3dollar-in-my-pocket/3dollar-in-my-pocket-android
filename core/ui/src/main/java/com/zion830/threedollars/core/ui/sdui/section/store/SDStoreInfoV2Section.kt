package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray10
import base.compose.Gray30
import base.compose.Gray50
import base.compose.Gray60
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.R as CommonR
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV2SectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV2SectionModel.DetailRowType
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.SDSectionHeader
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdClickable
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreInfoV2SectionDefaults {
    val ContentSpacing = 12.dp
    val CardShape = RoundedCornerShape(20.dp)
    val CardInset = 16.dp
    val GalleryItemWidth = 288.dp
    val GalleryItemHeight = 180.dp
    val GallerySpacing = 12.dp
    val GalleryShape = RoundedCornerShape(12.dp)
    val DetailRowSpacing = 8.dp
    val DetailLabelWidth = 104.dp
    val DetailRowHeight = 18.dp
    val DetailLabelValueSpacing = 12.dp
    val DetailTextSpacing = 2.dp
    val AccountTextSpacing = 2.dp
    val AccountDividerWidth = 1.dp
    val AccountDividerHeight = 12.dp
    val CopyButtonHeight = 34.dp
    val CopyButtonShape = RoundedCornerShape(10.dp)
    val CopyButtonPadding = PaddingValues(horizontal = 10.dp)
    val MenuItemSpacing = 16.dp
    val MenuImageSize = 44.dp
    val MenuImageTextSpacing = 8.dp
    val MenuTextSpacing = 2.dp
    val MenuEmptyHeight = 78.dp
    val MenuEmptyShape = RoundedCornerShape(6.dp)
    val MenuEmptyIconSize = 48.dp
    val MenuEmptyHorizontalPadding = 20.dp
    const val COLLAPSED_MENU_ITEM_COUNT = 6
}

/**
 * INFO_V2 섹션: 사장님 가게의 대표 사진 갤러리, 상세 정보(SNS·소개), 계좌 카드, 메뉴 목록.
 * 상세 정보나 메뉴가 없으면 빈 카드를 보여주고, 메뉴는 6개까지만 보여준 뒤 `메뉴 N개 더보기` 로 펼친다.
 *
 * @param onImageClick 갤러리 사진을 탭하면 전체 사진 목록과 탭한 위치를 넘긴다.
 */
@Composable
fun SDStoreInfoV2Section(
    model: SDStoreInfoV2SectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    onImageClick: (images: List<SDImageModel>, index: Int) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.ContentSpacing)
    ) {
        SDSectionHeader(model = model.header, onAction = onAction)
        val galleryImages = model.imageGallery?.images.orEmpty()
        if (galleryImages.isNotEmpty()) {
            ImageGallery(images = galleryImages, onImageClick = onImageClick)
        }
        Column(
            modifier = Modifier.padding(horizontal = SDSectionDefaults.HorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.ContentSpacing)
        ) {
            val detailCard = model.detailCard
            when {
                detailCard == null -> DetailEmptyCard()
                detailCard.rows.orEmpty().isNotEmpty() -> DetailCard(card = detailCard, onAction = onAction)
            }
            model.accountCards.orEmpty().forEach { AccountCard(card = it, onAction = onAction) }
            val menuListCard = model.menuListCard
            if (menuListCard != null && menuListCard.items.orEmpty().isNotEmpty()) {
                MenuListCard(card = menuListCard, stateKey = model.sectionId)
            } else {
                MenuEmptyView()
            }
        }
    }
}

@Composable
private fun ImageGallery(
    images: List<SDImageModel>,
    onImageClick: (List<SDImageModel>, Int) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = SDSectionDefaults.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.GallerySpacing)
    ) {
        itemsIndexed(images) { index, image ->
            SDImage(
                model = image,
                sizeFromStyle = false,
                modifier = Modifier
                    .size(
                        width = SDStoreInfoV2SectionDefaults.GalleryItemWidth,
                        height = SDStoreInfoV2SectionDefaults.GalleryItemHeight
                    )
                    .clip(SDStoreInfoV2SectionDefaults.GalleryShape)
                    .background(Gray10)
                    .noRippleClickable { onImageClick(images, index) }
            )
        }
    }
}

@Composable
private fun DetailCard(
    card: SDStoreInfoV2SectionModel.DetailCard,
    onAction: (SDActionEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStoreInfoV2SectionDefaults.CardShape)
            .padding(SDStoreInfoV2SectionDefaults.CardInset),
        verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.DetailRowSpacing)
    ) {
        card.rows.orEmpty().forEach { row ->
            when (row.type) {
                DetailRowType.LINK -> DetailLinkRow(row = row, onAction = onAction)
                DetailRowType.TEXT -> DetailTextRow(row)
                null -> Unit
            }
        }
    }
}

@Composable
private fun DetailLinkRow(
    row: SDStoreInfoV2SectionModel.DetailRow,
    onAction: (SDActionEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SDStoreInfoV2SectionDefaults.DetailRowHeight)
            .sdClickable(event = SDActionEvent(link = row.link), onAction = onAction),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(SDStoreInfoV2SectionDefaults.DetailLabelWidth)) {
            row.label?.let {
                SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W700, lineHeight = dpToSp(18), maxLines = 1)
            }
        }
        Spacer(modifier = Modifier.width(SDStoreInfoV2SectionDefaults.DetailLabelValueSpacing))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            row.value?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(12),
                    fontWeight = FontWeight.W500,
                    lineHeight = dpToSp(18),
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun DetailTextRow(row: SDStoreInfoV2SectionModel.DetailRow) {
    Column(verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.DetailTextSpacing)) {
        row.title?.let { SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W700, lineHeight = dpToSp(18)) }
        row.body?.let { SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, lineHeight = dpToSp(18)) }
    }
}

/**
 * 서버가 상세 정보 카드를 주지 않았을 때 항목 이름만 보여주는 빈 카드.
 */
@Composable
private fun DetailEmptyCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Gray0, shape = SDStoreInfoV2SectionDefaults.CardShape)
            .padding(SDStoreInfoV2SectionDefaults.CardInset),
        verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.DetailRowSpacing)
    ) {
        listOf(CommonR.string.sns, CommonR.string.owner_one_word).forEach { title ->
            Text(
                text = stringResource(title),
                color = Gray60,
                fontSize = dpToSp(12),
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily
            )
        }
    }
}

@Composable
private fun AccountCard(
    card: SDStoreInfoV2SectionModel.AccountCard,
    onAction: (SDActionEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStoreInfoV2SectionDefaults.CardShape)
            .padding(SDStoreInfoV2SectionDefaults.CardInset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.AccountTextSpacing)
        ) {
            card.title?.let {
                SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W700, lineHeight = dpToSp(18))
            }
            card.account?.let { account ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    account.text?.let {
                        SDText(
                            model = it,
                            fontSize = dpToSp(12),
                            fontWeight = FontWeight.W500,
                            lineHeight = dpToSp(18),
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    account.additionalText?.let {
                        Box(
                            modifier = Modifier
                                .size(
                                    width = SDStoreInfoV2SectionDefaults.AccountDividerWidth,
                                    height = SDStoreInfoV2SectionDefaults.AccountDividerHeight
                                )
                                .background(Gray30)
                        )
                        SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, lineHeight = dpToSp(18), maxLines = 1)
                    }
                }
            }
        }
        card.copyButton?.let { copyButton ->
            Spacer(modifier = Modifier.width(SDStoreInfoV2SectionDefaults.ContentSpacing))
            SDButton(
                model = copyButton,
                onAction = onAction,
                shape = SDStoreInfoV2SectionDefaults.CopyButtonShape,
                contentPadding = SDStoreInfoV2SectionDefaults.CopyButtonPadding,
                fontSize = dpToSp(12),
                modifier = Modifier.height(SDStoreInfoV2SectionDefaults.CopyButtonHeight)
            )
        }
    }
}

@Composable
private fun MenuListCard(card: SDStoreInfoV2SectionModel.MenuListCard, stateKey: String?) {
    var expanded by rememberSaveable(stateKey) { mutableStateOf(false) }
    val items = card.items.orEmpty()
    val collapsed = !expanded && items.size > SDStoreInfoV2SectionDefaults.COLLAPSED_MENU_ITEM_COUNT
    val visibleItems = if (collapsed) items.take(SDStoreInfoV2SectionDefaults.COLLAPSED_MENU_ITEM_COUNT) else items
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStoreInfoV2SectionDefaults.CardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SDStoreInfoV2SectionDefaults.CardInset),
            verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.MenuItemSpacing)
        ) {
            visibleItems.forEach { MenuItemRow(it) }
        }
        if (collapsed) {
            SDStoreMenuMoreButton(hiddenItemCount = items.size - visibleItems.size, onClick = { expanded = true })
        }
    }
}

@Composable
private fun MenuItemRow(item: SDStoreInfoV2SectionModel.MenuItem) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.MenuImageTextSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.image?.let { image ->
            SDImage(
                model = image,
                sizeFromStyle = false,
                modifier = Modifier
                    .size(SDStoreInfoV2SectionDefaults.MenuImageSize)
                    .clip(CircleShape)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(SDStoreInfoV2SectionDefaults.MenuTextSpacing)) {
            item.primaryText?.let {
                SDText(model = it, fontSize = dpToSp(14), fontWeight = FontWeight.W600, lineHeight = dpToSp(20))
            }
            item.secondaryText?.let {
                SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, lineHeight = dpToSp(18))
            }
        }
    }
}

@Composable
private fun MenuEmptyView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SDStoreInfoV2SectionDefaults.MenuEmptyHeight)
            .background(color = Gray0, shape = SDStoreInfoV2SectionDefaults.MenuEmptyShape)
            .padding(horizontal = SDStoreInfoV2SectionDefaults.MenuEmptyHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_photo_review_empty),
            contentDescription = null,
            modifier = Modifier.size(SDStoreInfoV2SectionDefaults.MenuEmptyIconSize)
        )
        Text(
            text = stringResource(CommonR.string.store_detail_boss_menu_empty),
            color = Gray50,
            fontSize = dpToSp(12),
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily
        )
    }
}
