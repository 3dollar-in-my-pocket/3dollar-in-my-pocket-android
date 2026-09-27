package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.section.SDStoreCtaSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreCtaSectionDefaults {
    val SubTitleSpacing = 2.dp
    val ButtonSpacing = 8.dp
}

/**
 * CTA 섹션: 제목·부제와 왼쪽 아래 텍스트 버튼(예: 사장님 앱 소개보기).
 */
@Composable
fun SDStoreCtaSection(
    model: SDStoreCtaSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val content = model.content ?: return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(horizontal = SDSectionDefaults.HorizontalPadding, vertical = SDSectionDefaults.VerticalPadding)
    ) {
        content.title?.let {
            SDText(model = it, fontSize = dpToSp(14), fontWeight = FontWeight.SemiBold)
        }
        content.subTitle?.let {
            SDText(
                model = it,
                fontSize = dpToSp(12),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = SDStoreCtaSectionDefaults.SubTitleSpacing)
            )
        }
        content.footerLeftButton?.let { button ->
            SDButton(
                model = button,
                onAction = onAction,
                fontSize = dpToSp(12),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = SDStoreCtaSectionDefaults.ButtonSpacing)
            )
        }
    }
}
