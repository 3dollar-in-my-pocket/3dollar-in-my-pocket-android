package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import base.compose.ColorWhite
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

/**
 * AD_MOB 섹션. 광고 SDK 는 앱 모듈에만 있으므로 배너는 [adContent] 슬롯으로 받는다.
 * 광고가 실패하면 자리를 비워야 하므로 여백도 슬롯이 정한다.
 */
@Composable
fun SDStoreAdmobSection(
    model: SDStoreAdmobSectionModel,
    modifier: Modifier = Modifier,
    adContent: @Composable (card: SDStoreAdmobSectionModel.Card, modifier: Modifier) -> Unit = { _, _ -> },
) {
    val card = model.cards?.firstOrNull() ?: return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
    ) {
        adContent(card, Modifier.fillMaxWidth())
    }
}
