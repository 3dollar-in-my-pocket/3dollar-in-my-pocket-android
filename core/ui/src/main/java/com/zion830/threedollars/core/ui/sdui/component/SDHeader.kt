package com.zion830.threedollars.core.ui.sdui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.dpToSp
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDText

/**
 * 섹션·카드 머리. 제목 아래 부제, 오른쪽에 텍스트 버튼(`trailingAction`)이 올 수 있다.
 */
@Composable
fun SDHeader(
    model: SDHeaderModel,
    modifier: Modifier = Modifier,
    onAction: (SDActionEvent) -> Unit = {},
    titleMaxLines: Int = 1
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            model.title?.let {
                SDText(model = it, maxLines = titleMaxLines, fontSize = dpToSp(16), fontWeight = FontWeight.W700)
            }
            model.subTitle?.let {
                SDText(model = it, maxLines = 1, fontSize = dpToSp(12))
            }
        }
        model.trailingAction?.let { action ->
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                SDButton(
                    model = action,
                    onAction = onAction,
                    fontSize = dpToSp(12),
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * 섹션 머리의 기본 배치(좌우 20, 전체 너비).
 */
@Composable
fun SDSectionHeader(
    model: SDHeaderModel?,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    model ?: return
    SDHeader(
        model = model,
        onAction = onAction,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SDSectionDefaults.HorizontalPadding)
    )
}
