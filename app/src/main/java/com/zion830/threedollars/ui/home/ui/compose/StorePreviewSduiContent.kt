package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.compose.Gray100
import base.compose.Pink
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStorePreviewSectionSlots
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 홈 미리보기 시트(tip) 본문. 상세 PREVIEW 섹션과 같은 [SDStorePreviewSection] 으로 그려서 tip → full 로 바뀔 때
 * 가게명·메타·버튼·사진 위치가 그대로이고 아래 내용만 붙는다. tip 에만 있는 저장·닫기 버튼, 사진 추가 칸, 리뷰 캐러셀은 슬롯으로 붙인다.
 *
 * @param showHeaderButtons full 로 올라가는 중·응답 전 자리표시로 쓸 때는 상단 네비가 같은 버튼을 가지므로 false.
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
    SDStorePreviewSection(
        model = preview,
        onAction = onAction,
        onImageClick = { images, index -> onImageClick(images.mapNotNull { it.url }, index) },
        slots = slots,
        modifier = modifier,
    )
}
