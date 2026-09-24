package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.section.SDStoreEditSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDButtonDefaults
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreEditSectionDefaults {
    val MapHeight = 140.dp
    val MapShape = RoundedCornerShape(20.dp)
    val OverlayMargin = 8.dp
    val AddressHeight = 34.dp
    val EnlargeButtonSize = 36.dp
    val EditBarHeight = 44.dp
    val EditBarShape = RoundedCornerShape(12.dp)
    val Spacing = 12.dp
}

/**
 * EDIT 섹션: 가게 위치 지도(주소 복사·크게 보기 버튼 포함)와 정보 수정·신고 버튼 줄.
 * 지도 SDK 는 앱 모듈에만 있으므로 지도 자체는 [mapContent] 슬롯으로 받는다.
 */
@Composable
fun SDStoreEditSection(
    model: SDStoreEditSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    mapContent: @Composable (latitude: Double, longitude: Double, modifier: Modifier) -> Unit = { _, _, _ -> },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(horizontal = SDSectionDefaults.HorizontalPadding, vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDStoreEditSectionDefaults.Spacing)
    ) {
        model.map?.let { map -> EditMap(map = map, onAction = onAction, mapContent = mapContent) }
        val actionBars = model.actionBars.orEmpty().filter { it.button != null }
        if (actionBars.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SDStoreEditSectionDefaults.Spacing)
            ) {
                actionBars.forEach { bar ->
                    SDButton(
                        model = requireNotNull(bar.button),
                        onAction = onAction,
                        fallbackLog = bar.clickLog,
                        shape = SDStoreEditSectionDefaults.EditBarShape,
                        fontSize = dpToSp(13),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .weight(1f)
                            .height(SDStoreEditSectionDefaults.EditBarHeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun EditMap(
    map: SDStoreEditSectionModel.Map,
    onAction: (SDActionEvent) -> Unit,
    mapContent: @Composable (Double, Double, Modifier) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SDStoreEditSectionDefaults.MapHeight)
            .clip(SDStoreEditSectionDefaults.MapShape)
    ) {
        val latitude = map.location?.latitude
        val longitude = map.location?.longitude
        if (latitude != null && longitude != null) {
            mapContent(latitude, longitude, Modifier.matchParentSize())
        }
        map.footerLeft?.button?.let { address ->
            SDButton(
                model = address,
                onAction = onAction,
                fallbackLog = map.footerLeft?.clickLog,
                shape = SDButtonDefaults.PillShape,
                contentPadding = PaddingValues(horizontal = 12.dp),
                fontSize = dpToSp(12),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = SDStoreEditSectionDefaults.OverlayMargin,
                        end = SDStoreEditSectionDefaults.EnlargeButtonSize + SDStoreEditSectionDefaults.OverlayMargin * 2,
                        bottom = SDStoreEditSectionDefaults.OverlayMargin
                    )
                    .height(SDStoreEditSectionDefaults.AddressHeight)
            )
        }
        map.footerRight?.button?.let { enlarge ->
            SDButton(
                model = enlarge,
                onAction = onAction,
                fallbackLog = map.footerRight?.clickLog,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(SDStoreEditSectionDefaults.OverlayMargin)
                    .shadow(elevation = 2.dp, shape = CircleShape)
                    .size(SDStoreEditSectionDefaults.EnlargeButtonSize)
            )
        }
    }
}
