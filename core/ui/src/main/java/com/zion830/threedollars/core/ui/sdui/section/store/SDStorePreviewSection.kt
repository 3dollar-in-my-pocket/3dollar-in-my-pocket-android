package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.dpToSp
import com.threedollar.common.serverdriven.PreviewImageLayout
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.component.SDActionBarRow
import com.zion830.threedollars.core.ui.sdui.component.SDChipRowSeparator
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStorePreviewSectionDefaults {
    val MetadataRowHeight = 20.dp
    const val TITLE_LINE_HEIGHT = 28
    val TitleMetadataSpacing = 4.dp
    val ContributorHeight = 28.dp
    val ImageSpacing = 8.dp
    val ImageShape = RoundedCornerShape(12.dp)
    const val DEFAULT_IMAGE_SIZE = 120f
    val BodyCarouselItemWidth = 300.dp
    val BodyCarouselSpacing = 4.dp
    val BodyCarouselShape = RoundedCornerShape(12.dp)
    val BodyCarouselPadding = PaddingValues(horizontal = 12.dp, vertical = 11.dp)
    const val BODY_CAROUSEL_LINE_HEIGHT = 18
    const val BODY_CAROUSEL_MAX_LINES = 2
}

/**
 * PREVIEW 섹션을 쓰는 자리마다 다른 부분. 홈 미리보기 시트(tip)는 가게명 옆 저장·닫기 버튼, 사진 줄 끝 사진 추가 칸,
 * 가로로 넘기는 리뷰 카드를 붙이고, 상세(full)는 아무것도 붙이지 않는다. 나머지는 같은 레이아웃이라 tip → full 전환 때 위치가 그대로다.
 *
 * @param headerTrailing 가게명 줄 오른쪽 위에 겹쳐 그린다. 줄 높이에 영향을 주지 않아 아래 내용 위치가 full 과 같다.
 * 가게명은 [headerTrailingWidth] 만큼 비워 둔다.
 * @param imagesTrailing 사진 줄 끝에 붙는 칸. 사진 줄 높이를 받는다.
 * @param bodiesAsCarousel true 면 본문을 가로 캐러셀(카드 300dp, 하나면 전체 폭)로, false 면 세로로 쌓는다.
 */
data class SDStorePreviewSectionSlots(
    val headerTrailing: (@Composable () -> Unit)? = null,
    val headerTrailingWidth: Dp = 0.dp,
    val imagesTrailing: (@Composable (rowHeight: Dp) -> Unit)? = null,
    val bodiesAsCarousel: Boolean = false,
)

/**
 * PREVIEW 섹션: 가게명·배지, 메타데이터 두 줄, 기여자 버튼, 액션 버튼 줄, 사진 줄, 본문.
 *
 * @param actionBarsModifier 하단 고정 칩 바 노출 판단을 위해 화면이 액션 버튼 줄의 위치를 재는 데 쓴다.
 * @param onImageClick 사진을 탭하면 전체 사진 목록과 탭한 위치를 넘긴다.
 */
@Composable
fun SDStorePreviewSection(
    model: SDStorePreviewSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    actionBarsModifier: Modifier = Modifier,
    onImageClick: (images: List<SDImageModel>, index: Int) -> Unit = { _, _ -> },
    slots: SDStorePreviewSectionSlots = SDStorePreviewSectionSlots(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(SDStorePreviewSectionDefaults.TitleMetadataSpacing)) {
            PreviewTitle(header = model.header, trailing = slots.headerTrailing, trailingWidth = slots.headerTrailingWidth)
            Column(modifier = Modifier.padding(horizontal = SDSectionDefaults.HorizontalPadding)) {
                MetadataRow(chips = model.metadata?.primary.orEmpty(), separator = model.metadata?.separator)
                MetadataRow(chips = model.metadata?.secondary.orEmpty(), separator = model.metadata?.separator)
            }
        }
        model.contributorActionBar?.button?.let { contributor ->
            SDButton(
                model = contributor,
                onAction = onAction,
                fallbackLog = model.contributorActionBar?.clickLog,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(horizontal = SDSectionDefaults.HorizontalPadding)
                    .height(SDStorePreviewSectionDefaults.ContributorHeight)
            )
        }
        model.actionBars?.takeIf { it.isNotEmpty() }?.let { actionBars ->
            SDActionBarRow(
                actionBars = actionBars,
                onAction = onAction,
                modifier = actionBarsModifier.padding(top = 6.dp)
            )
        }
        model.images?.takeIf { it.isNotEmpty() }?.let { images ->
            PreviewImages(images = images, onImageClick = onImageClick, trailing = slots.imagesTrailing)
        }
        if (slots.bodiesAsCarousel) {
            BodyCarousel(bodies = model.bodies.orEmpty())
        } else model.bodies.orEmpty().forEach { body ->
            body.text?.let { text ->
                SDText(
                    model = text,
                    fontSize = dpToSp(14),
                    modifier = Modifier
                        .padding(horizontal = SDSectionDefaults.HorizontalPadding)
                        .fillMaxWidth()
                        .sdSurface(style = body.style, shape = SDSectionDefaults.SmallCardShape)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun PreviewTitle(
    header: SDStorePreviewSectionModel.Header?,
    trailing: (@Composable () -> Unit)?,
    trailingWidth: Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SDSectionDefaults.HorizontalPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = trailingWidth),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            header?.title?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(20),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = dpToSp(SDStorePreviewSectionDefaults.TITLE_LINE_HEIGHT),
                    maxLines = 2,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            header?.badge?.let { SDImage(it) }
        }
        if (trailing != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .wrapContentSize(align = Alignment.TopEnd, unbounded = true)
            ) { trailing() }
        }
    }
}

@Composable
private fun MetadataRow(chips: List<SDChipModel>, separator: SDImageModel?) {
    if (chips.isEmpty()) return
    Row(
        modifier = Modifier.height(SDStorePreviewSectionDefaults.MetadataRowHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        chips.forEachIndexed { index, chip ->
            SDChip(model = chip, fontSize = dpToSp(14), lineHeight = dpToSp(20))
            if (index < chips.lastIndex) SDChipRowSeparator(separator)
        }
    }
}

@Composable
private fun PreviewImages(
    images: List<SDImageModel>,
    onImageClick: (List<SDImageModel>, Int) -> Unit,
    trailing: (@Composable (rowHeight: Dp) -> Unit)?,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val spacing = SDStorePreviewSectionDefaults.ImageSpacing
        val available = maxWidth - SDSectionDefaults.HorizontalPadding * 2
        val fillWidth = PreviewImageLayout.fillWidth(images.size, available.value, spacing.value)
        LazyRow(
            contentPadding = PaddingValues(horizontal = SDSectionDefaults.HorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            itemsIndexed(images) { index, image ->
                val width = fillWidth ?: image.style?.width ?: SDStorePreviewSectionDefaults.DEFAULT_IMAGE_SIZE
                val height = image.style?.height ?: SDStorePreviewSectionDefaults.DEFAULT_IMAGE_SIZE
                SDImage(
                    model = image,
                    sizeFromStyle = false,
                    modifier = Modifier
                        .size(width = width.dp, height = height.dp)
                        .clip(SDStorePreviewSectionDefaults.ImageShape)
                        .noRippleClickable { onImageClick(images, index) }
                )
            }
            if (trailing != null) {
                item(key = "images-trailing") {
                    trailing(images.maxOf { (it.style?.height ?: SDStorePreviewSectionDefaults.DEFAULT_IMAGE_SIZE).toFloat() }.dp)
                }
            }
        }
    }
}

@Composable
private fun BodyCarousel(bodies: List<SDStorePreviewSectionModel.Body>) {
    val texts = bodies.mapNotNull { body -> body.text?.let { body to it } }
    if (texts.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = SDSectionDefaults.HorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(SDStorePreviewSectionDefaults.BodyCarouselSpacing)
    ) {
        itemsIndexed(texts) { _, (body, text) ->
            SDText(
                model = text,
                fontSize = dpToSp(12),
                lineHeight = dpToSp(SDStorePreviewSectionDefaults.BODY_CAROUSEL_LINE_HEIGHT),
                maxLines = SDStorePreviewSectionDefaults.BODY_CAROUSEL_MAX_LINES,
                modifier = Modifier
                    .then(
                        if (texts.size == 1) {
                            Modifier.fillParentMaxWidth()
                        } else {
                            Modifier.width(SDStorePreviewSectionDefaults.BodyCarouselItemWidth)
                        }
                    )
                    .sdSurface(style = body.style, shape = SDStorePreviewSectionDefaults.BodyCarouselShape)
                    .padding(SDStorePreviewSectionDefaults.BodyCarouselPadding)
            )
        }
    }
}
