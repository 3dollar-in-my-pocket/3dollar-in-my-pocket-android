package com.zion830.threedollars.core.ui.sdui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDButtonDefaults

object SDActionBarRowDefaults {
    val ChipHeight = 36.dp
    val ChipPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    val Spacing = 4.dp
}

/**
 * 방문 인증·리뷰 작성·공유·길안내 같은 알약 모양 버튼 줄. PREVIEW 섹션과 하단 고정 칩 바가 같이 쓴다.
 * 넘치면 가로로 스크롤된다.
 */
@Composable
fun SDActionBarRow(
    actionBars: List<SDActionBarModel>,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    spacing: Dp = SDActionBarRowDefaults.Spacing
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        actionBars.forEach { bar ->
            val button = bar.button ?: return@forEach
            SDButton(
                model = button,
                onAction = onAction,
                fallbackLog = bar.clickLog,
                shape = SDButtonDefaults.PillShape,
                contentPadding = SDActionBarRowDefaults.ChipPadding,
                modifier = Modifier.height(SDActionBarRowDefaults.ChipHeight)
            )
        }
    }
}
