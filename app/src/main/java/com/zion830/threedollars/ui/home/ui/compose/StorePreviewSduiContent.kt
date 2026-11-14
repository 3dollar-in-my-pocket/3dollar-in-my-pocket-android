package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.Gray100
import base.compose.Pink
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewMoreSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreAdmobSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewMoreSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewMoreSectionDefaults
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewSectionSlots
import com.zion830.threedollars.ui.ads.StoreAdMobBanner
import com.zion830.threedollars.ui.storeDetail.sdui.model.StorePreviewSections
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 홈 미리보기 시트(tip) 본문. 상세 PREVIEW 섹션과 같은 [SDStorePreviewSection] 으로 그려서 tip → full 로 바뀔 때
 * 가게명·메타·버튼·사진 위치가 그대로이고 아래 내용만 붙는다. tip 에만 있는 저장·닫기 버튼, 사진 추가 칸, 리뷰 캐러셀은 슬롯으로 붙인다.
 *
 * @param showHeaderButtons full 로 올라가는 중·응답 전 자리표시로 쓸 때는 상단 네비가 같은 버튼을 가지므로 false.
 * @param sections 서버 순서대로 그릴 섹션(`StorePreviewSections`). PREVIEW 자리에 [preview] 를, AD_MOB 자리에 배너를 그린다.
 * 비어 있거나 PREVIEW 가 없으면 [preview] 만 그린다. PREVIEW_MORE 는 맨 아래에 그리되 [PeekBottomLayout] 으로
 * 높이를 줄여 보고하므로, 시트가 그 높이에 맞춰 멈추면 티저 윗부분만 보이고 나머지는 화면 밖으로 잘린다(TH-1584).
 */
@Composable
internal fun StorePreviewSduiContent(
    preview: SDStorePreviewSectionModel,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClose: () -> Unit,
    onAction: (SDActionEvent) -> Unit,
    onImageClick: (imageUrls: List<String>, index: Int) -> Unit,
    onAddPhotoClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    showHeaderButtons: Boolean = true,
    sections: List<SDSectionModel> = emptyList(),
    onAdClick: (SDLogModel?) -> Unit = {},
    onImpression: (key: String, log: SDLogModel) -> Unit = { _, _ -> },
) {
    val canSave = preview.additionalInfos != null
    val buttonCount = if (canSave) 2 else 1
    val headerTrailingWidth = if (showHeaderButtons) {
        StorePreviewIconButtonSize * buttonCount + StorePreviewIconButtonGap * buttonCount
    } else {
        0.dp
    }
    val slots = remember(showHeaderButtons, canSave, isFavorite, onAddPhotoClick) {
        SDStorePreviewSectionSlots(
            headerTrailing = if (showHeaderButtons) {
                {
                    Row(horizontalArrangement = Arrangement.spacedBy(StorePreviewIconButtonGap)) {
                        if (canSave) {
                            StorePreviewIconButton(
                                iconRes = if (isFavorite) {
                                    DesignSystemR.drawable.ic_store_preview_bookmark_solid
                                } else {
                                    DesignSystemR.drawable.ic_store_preview_bookmark_line
                                },
                                tint = if (isFavorite) Pink else Gray100,
                                onClick = onFavoriteClick,
                            )
                        }
                        StorePreviewIconButton(
                            iconRes = DesignSystemR.drawable.ic_store_preview_close,
                            tint = Gray100,
                            onClick = onClose,
                        )
                    }
                }
            } else {
                null
            },
            headerTrailingWidth = headerTrailingWidth,
            imagesTrailing = onAddPhotoClick?.let { onAdd -> { rowHeight -> AddPhotoTile(tileSize = rowHeight, onClick = onAdd) } },
            bodiesAsCarousel = true,
        )
    }
    val orderedSections = sections.takeIf { list -> list.any { it is SDStorePreviewSectionModel } } ?: listOf(preview)
    val moreIndex = orderedSections.indexOfLast { it is SDStorePreviewMoreSectionModel }
    val more = orderedSections.getOrNull(moreIndex) as? SDStorePreviewMoreSectionModel
    PeekBottomLayout(
        peekHeight = SDStorePreviewMoreSectionDefaults.PeekHeight,
        modifier = modifier,
        body = {
            Column {
                orderedSections.forEachIndexed { index, section ->
                    when (section) {
                        is SDStorePreviewSectionModel -> SDStorePreviewSection(
                            model = preview,
                            onAction = onAction,
                            onImageClick = { images, imageIndex -> onImageClick(images.mapNotNull { it.url }, imageIndex) },
                            slots = slots,
                        )
                        is SDStoreAdmobSectionModel -> StorePreviewAdMobSection(
                            index = index,
                            section = section,
                            onAdClick = onAdClick,
                            onImpression = onImpression,
                        )
                        else -> Unit
                    }
                }
            }
        },
        peek = more?.let { section ->
            { StorePreviewMoreSection(index = moreIndex, section = section, onImpression = onImpression) }
        },
    )
}

/**
 * [body] 아래에 [peek] 를 붙이되, 부모에게는 [peek] 를 [peekHeight] 까지만 포함한 높이를 알린다.
 * 넘친 부분은 그대로 그려지지만 시트가 알린 높이에 맞춰 멈추므로 화면 아래로 잘려 보인다.
 */
@Composable
private fun PeekBottomLayout(
    peekHeight: Dp,
    body: @Composable () -> Unit,
    peek: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Layout(
        content = {
            body()
            peek?.invoke()
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val childConstraints = constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
        val bodyPlaceable = measurables[0].measure(childConstraints)
        val peekPlaceable = measurables.getOrNull(1)?.measure(childConstraints)
        val width = maxOf(bodyPlaceable.width, peekPlaceable?.width ?: 0).coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = StorePreviewSections.peekLayoutHeight(
            bodyPx = bodyPlaceable.height,
            peekPx = peekPlaceable?.height,
            maxPeekPx = peekHeight.roundToPx(),
        ).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            bodyPlaceable.placeRelative(0, 0)
            peekPlaceable?.placeRelative(0, bodyPlaceable.height)
        }
    }
}

/** 티저 노출 로그는 서버가 준 값만 보낸다. 상세 노출과 겹치지 않게 AD_MOB 과 같은 접두를 붙인다. */
@Composable
private fun StorePreviewMoreSection(
    index: Int,
    section: SDStorePreviewMoreSectionModel,
    onImpression: (key: String, log: SDLogModel) -> Unit,
) {
    val logs = StorePreviewSections.impressionLogs(index, section)
    LaunchedEffect(logs) {
        logs.forEach { (key, log) -> onImpression(key, log) }
    }
    SDStorePreviewMoreSection(model = section)
}

/** 미리보기 광고는 시트에 바로 보이므로 상세와 달리 VISIBLE 로 요청한다. 노출 로그 키는 상세와 겹치지 않게 접두를 붙인다. */
@Composable
private fun StorePreviewAdMobSection(
    index: Int,
    section: SDStoreAdmobSectionModel,
    onAdClick: (SDLogModel?) -> Unit,
    onImpression: (key: String, log: SDLogModel) -> Unit,
) {
    val logs = StorePreviewSections.impressionLogs(index, section)
    LaunchedEffect(logs) {
        logs.forEach { (key, log) -> onImpression(key, log) }
    }
    SDStoreAdmobSection(
        model = section,
        adContent = { card, adModifier -> StoreAdMobBanner(card = card, onAdClick = onAdClick, modifier = adModifier) },
    )
}
