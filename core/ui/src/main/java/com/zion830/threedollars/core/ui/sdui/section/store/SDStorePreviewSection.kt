package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
    val ContributorHeight = 28.dp
    val ImageSpacing = 8.dp
    val ImageShape = RoundedCornerShape(12.dp)
    const val DEFAULT_IMAGE_SIZE = 120f
}

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
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PreviewTitle(model.header)
        Column(modifier = Modifier.padding(horizontal = SDSectionDefaults.HorizontalPadding)) {
            MetadataRow(chips = model.metadata?.primary.orEmpty(), separator = model.metadata?.separator)
            MetadataRow(chips = model.metadata?.secondary.orEmpty(), separator = model.metadata?.separator)
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
            PreviewImages(images = images, onImageClick = onImageClick)
        }
        model.bodies.orEmpty().forEach { body ->
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
private fun PreviewTitle(header: SDStorePreviewSectionModel.Header?) {
    header ?: return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SDSectionDefaults.HorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        header.title?.let {
            SDText(
                model = it,
                fontSize = dpToSp(20),
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
        header.badge?.let { SDImage(it) }
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
    onImageClick: (List<SDImageModel>, Int) -> Unit
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
        }
    }
}
