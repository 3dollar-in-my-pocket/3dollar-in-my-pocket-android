package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.compose.Gray0
import com.threedollar.common.sdui.model.section.SDStoreMarginSectionModel

/**
 * 섹션 사이 구분 띠. 상세 화면의 섹션 간격은 오직 이 섹션으로만 표현한다.
 * 서버는 흰 배경을 내려주지만 구분 띠가 보이도록 iOS 와 같이 gray0 으로 고정한다.
 */
@Composable
fun SDStoreMarginSection(
    model: SDStoreMarginSectionModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height((model.height ?: 0).dp)
            .background(Gray0)
    )
}
