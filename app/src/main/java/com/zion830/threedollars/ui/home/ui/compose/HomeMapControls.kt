package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.ColorBlack
import base.compose.ColorWhite
import base.compose.Gray20
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.serverdriven.model.HomeMapControlButton
import com.zion830.threedollars.ui.home.data.HomeMapControlItem
import com.zion830.threedollars.ui.home.ui.HomeSheetLayout
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private val ControlButtonSize = 48.dp
private val ShadowInset = HomeSheetLayout.MAP_CONTROL_SHADOW_INSET_DP.dp
private val ControlSpacing = 8.dp
private const val DIMMED_ALPHA = 0.5f

/** 홈 지도 좌측 하단 컨트롤. 서버가 내려준 순서대로 위에서 아래로 쌓는다. */
@Composable
fun HomeMapControlColumn(
    items: List<HomeMapControlItem>,
    onClick: (HomeMapControlItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(ShadowInset),
        verticalArrangement = Arrangement.spacedBy(ControlSpacing),
    ) {
        items.forEach { item ->
            when (item) {
                is HomeMapControlItem.ServerDriven -> ServerDrivenControlButton(
                    button = item.button,
                    onClick = { onClick(item) },
                )

                HomeMapControlItem.FallbackCurrentLocation -> FallbackCurrentLocationButton(
                    onClick = { onClick(item) },
                )
            }
        }
    }
}

@Composable
private fun ServerDrivenControlButton(
    button: HomeMapControlButton,
    onClick: () -> Unit,
) {
    val border = button.style?.border
    ControlCircle(
        backgroundColor = button.style?.backgroundColor.toColor(fallback = ColorWhite),
        borderColor = border?.color.toColor(fallback = Gray20),
        borderWidth = (border?.width ?: 1.0).dp,
        onClick = onClick,
    ) {
        val imageStyle = button.image.style
        AsyncImage(
            model = button.image.url,
            contentDescription = button.clickLog?.objectId,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(
                    width = (imageStyle?.width ?: 28.0).dp,
                    height = (imageStyle?.height ?: 28.0).dp,
                )
                .alpha(if (imageStyle?.dimmed == true) DIMMED_ALPHA else 1f),
        )
    }
}

@Composable
private fun FallbackCurrentLocationButton(onClick: () -> Unit) {
    ControlCircle(
        backgroundColor = ColorWhite,
        borderColor = Gray20,
        borderWidth = 1.dp,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_search),
            contentDescription = null,
            tint = ColorBlack,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun ControlCircle(
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(ControlButtonSize)
            .floatingSurface(shape = CircleShape, elevation = 2.dp)
            .background(backgroundColor)
            .border(BorderStroke(borderWidth, borderColor), CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** 홈 지도 우측 하단 [+ 가게 제보] 버튼. */
@Composable
fun HomeWriteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)
    Row(
        modifier = modifier
            .padding(ShadowInset)
            .height(44.dp)
            .floatingSurface(shape = shape, elevation = 4.dp)
            .background(Pink)
            .border(BorderStroke(1.dp, ColorBlack.copy(alpha = 0.08f)), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 12.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_plus),
            contentDescription = null,
            tint = ColorWhite,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(CommonR.string.title_add_store),
            color = ColorWhite,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(16),
        )
    }
}

private fun Modifier.floatingSurface(shape: Shape, elevation: Dp): Modifier =
    shadow(elevation = elevation, shape = shape).clip(shape)
