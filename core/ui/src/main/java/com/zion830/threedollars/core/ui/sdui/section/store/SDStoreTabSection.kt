package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray60
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.toActionEvent
import com.threedollar.common.sdui.model.section.SDStoreTabSectionModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreTabSectionDefaults {
    val Height = 48.dp
    val IndicatorHeight = 2.dp
}

/**
 * TAB 섹션. 선택 상태는 스크롤 위치로 정해지므로 화면이 [selectedIndex]로 넘겨준다.
 * 글자·링크는 서버 값을 쓰고, 선택 표시(굵기·밑줄·색)만 클라이언트가 정한다.
 */
@Composable
fun SDStoreTabSection(
    model: SDStoreTabSectionModel,
    selectedIndex: Int,
    onTabClick: (index: Int, event: SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = model.tabs.orEmpty()
    val listState = rememberLazyListState()
    LaunchedEffect(selectedIndex) {
        if (selectedIndex in tabs.indices) listState.animateScrollToItem(selectedIndex)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(SDStoreTabSectionDefaults.Height - 1.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(tabs) { index, tab ->
                val text = tab.button?.text ?: return@itemsIndexed
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .height(SDStoreTabSectionDefaults.Height - 1.dp)
                        .noRippleClickable { tab.toActionEvent()?.let { onTabClick(index, it) } }
                        .padding(horizontal = 12.dp)
                        .drawBehind {
                            if (!selected) return@drawBehind
                            val indicatorHeight = SDStoreTabSectionDefaults.IndicatorHeight.toPx()
                            drawRect(
                                color = Gray100,
                                topLeft = Offset(0f, size.height - indicatorHeight),
                                size = Size(size.width, indicatorHeight)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    SDText(
                        model = text,
                        color = if (selected) Gray100 else Gray60,
                        fontSize = dpToSp(16),
                        fontWeight = if (selected) FontWeight.W700 else FontWeight.W500,
                        maxLines = 1
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Gray10)
        )
    }
}
