package com.zion830.threedollars.core.ui.sdui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import base.compose.Gray10
import base.compose.dpToSp
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabsModel
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

private object SDHomeBottomSheetTabsDefaults {
    val Height = 48.dp
    val TabHeight = 40.dp
    val Padding = 4.dp
    val Shape = RoundedCornerShape(percent = 50)
}

/** 탭의 선택 상태는 호출부가 소유하고, 선택별 텍스트와 스타일은 서버 값을 따른다. */
@Composable
fun SDHomeBottomSheetTabs(
    model: SDHomeBottomSheetTabsModel,
    selectedTabId: String?,
    onTabClick: (SDHomeBottomSheetTabModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (model.tabs.isEmpty()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SDHomeBottomSheetTabsDefaults.Height)
            .background(Gray10, SDHomeBottomSheetTabsDefaults.Shape)
            .padding(SDHomeBottomSheetTabsDefaults.Padding)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        model.tabs.forEach { tab ->
            val selected = tab.tabId == selectedTabId
            val appearance = if (selected) tab.selected else tab.unselected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(SDHomeBottomSheetTabsDefaults.TabHeight)
                    .sdSurface(appearance.style, SDHomeBottomSheetTabsDefaults.Shape)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onTabClick(tab) })
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                appearance.title?.let { SDText(it, fontSize = dpToSp(16), lineHeight = dpToSp(24), maxLines = 1) }
            }
        }
    }
}
