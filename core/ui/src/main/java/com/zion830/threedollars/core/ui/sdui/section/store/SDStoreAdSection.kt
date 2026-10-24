package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import base.compose.ColorWhite
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.toActionEvent
import com.threedollar.common.sdui.model.section.SDStoreAdSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.card.SDAdBannerCard
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

@Composable
fun SDStoreAdSection(
    model: SDStoreAdSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val card = model.firstAdCard ?: return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(horizontal = SDSectionDefaults.HorizontalPadding, vertical = SDSectionDefaults.VerticalPadding),
    ) {
        SDAdBannerCard(model = card, onClick = { onAction(it.toActionEvent()) })
    }
}
