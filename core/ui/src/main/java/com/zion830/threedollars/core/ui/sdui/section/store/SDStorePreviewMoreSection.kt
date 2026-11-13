package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import com.threedollar.common.sdui.model.section.SDStorePreviewMoreSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.SDSectionHeader
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

/**
 * PREVIEW_MORE 섹션. 상세 섹션과 같은 머리 배치로 그리고, 아래에 내용이 이어지는 것처럼 여백을 둔다.
 * 시트는 [SDStorePreviewMoreSectionDefaults.PeekHeight] 만큼만 보여 주고 나머지는 화면 밖으로 잘린다.
 */
@Composable
fun SDStorePreviewMoreSection(
    model: SDStorePreviewMoreSectionModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(top = SDSectionDefaults.VerticalPadding),
    ) {
        SDSectionHeader(model = model.header, onAction = {})
        Spacer(modifier = Modifier.height(SDStorePreviewMoreSectionDefaults.HiddenBodyHeight))
    }
}

object SDStorePreviewMoreSectionDefaults {
    /** 미리보기 시트 하단에 보이는 높이. 위 여백 16 + 제목 한 줄이 보이고 그 아래는 잘린다. */
    val PeekHeight = 52.dp

    /** 잘린 아래쪽에 내용이 더 있다는 느낌을 주는 여백. 시트에는 보이지 않는다. */
    val HiddenBodyHeight = 40.dp
}
