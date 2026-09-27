package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults

private const val SHIMMER_DURATION_MS = 1200

/**
 * 상세 응답 전 미리보기 헤더 아래에 보여주는 자리 표시(탭 줄·지도·버튼·본문 줄).
 */
@Composable
fun SDStoreSkeletonSection(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "store_skeleton")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(SHIMMER_DURATION_MS / 2), RepeatMode.Reverse),
        label = "store_skeleton_alpha"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .alpha(alpha)
            .padding(horizontal = SDSectionDefaults.HorizontalPadding, vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { SkeletonBlock(width = 48.dp, height = 20.dp) }
        }
        SkeletonBlock(height = SDStoreEditSectionDefaults.MapHeight, shape = SDStoreEditSectionDefaults.MapShape)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonBlock(height = SDStoreEditSectionDefaults.EditBarHeight, modifier = Modifier.weight(1f))
            SkeletonBlock(height = SDStoreEditSectionDefaults.EditBarHeight, modifier = Modifier.weight(1f))
        }
        SkeletonBlock(width = 120.dp, height = 20.dp)
        repeat(4) { SkeletonBlock(height = 16.dp) }
        SkeletonBlock(width = 200.dp, height = 16.dp)
    }
}

@Composable
private fun SkeletonBlock(
    height: Dp,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    color: Color = Gray10,
) {
    Box(
        modifier = modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .background(color, shape)
    )
}
