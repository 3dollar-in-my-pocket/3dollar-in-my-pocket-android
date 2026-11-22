package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.zion830.threedollars.core.designsystem.R

enum class AppChipStyle {
    Filled,
    Outline,
    Filter,
}

enum class AppChipSize {
    Medium,
    Small,
}

@Immutable
data class AppChipColors(
    val background: Color,
    val border: Color?,
    val content: Color,
    val borderWidth: Dp = 1.dp,
)

@Immutable
private data class AppChipMetrics(
    val height: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val shape: Shape,
)

private fun metricsOf(style: AppChipStyle, size: AppChipSize): AppChipMetrics = when (style) {
    AppChipStyle.Filled -> when (size) {
        AppChipSize.Medium -> AppChipMetrics(40.dp, 12.dp, 24.dp, RoundedCornerShape(50))
        AppChipSize.Small -> AppChipMetrics(32.dp, 8.dp, 24.dp, RoundedCornerShape(50))
    }
    AppChipStyle.Outline -> AppChipMetrics(36.dp, 12.dp, 20.dp, RoundedCornerShape(50))
    AppChipStyle.Filter -> AppChipMetrics(34.dp, 10.dp, 20.dp, RoundedCornerShape(10.dp))
}

fun appChipColors(style: AppChipStyle, selected: Boolean): AppChipColors = when (style) {
    AppChipStyle.Filled -> if (selected) {
        AppChipColors(background = Pink200, border = null, content = Pink)
    } else {
        AppChipColors(background = Gray10, border = null, content = Gray100)
    }
    AppChipStyle.Outline -> if (selected) {
        AppChipColors(background = Pink100, border = Pink500, content = Pink500)
    } else {
        AppChipColors(background = ColorWhite, border = Gray30, content = Gray90)
    }
    AppChipStyle.Filter -> if (selected) {
        AppChipColors(background = Pink100, border = Pink500, content = Pink500)
    } else {
        AppChipColors(background = ColorWhite, border = Gray30, content = Gray40)
    }
}

@Composable
fun AppChip(
    text: String,
    modifier: Modifier = Modifier,
    style: AppChipStyle = AppChipStyle.Filled,
    size: AppChipSize = AppChipSize.Medium,
    selected: Boolean = false,
    colors: AppChipColors = appChipColors(style, selected),
    onClick: (() -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null,
    closeContentDescription: String? = null,
    leadingIcon: (@Composable (Modifier) -> Unit)? = null,
) {
    val metrics = metricsOf(style, size)
    Row(
        modifier = modifier
            .height(metrics.height)
            .clip(metrics.shape)
            .background(colors.background)
            .then(
                if (colors.border != null) {
                    Modifier.border(colors.borderWidth, colors.border, metrics.shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null) {
                    Modifier.selectable(selected = selected, role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = metrics.horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.invoke(Modifier.size(metrics.iconSize))
        Text(
            text = text,
            color = colors.content,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(14),
            lineHeight = dpToSp(20),
            letterSpacing = (-0.01).em,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (onCloseClick != null) {
            Icon(
                painter = painterResource(R.drawable.ic_close_chip),
                contentDescription = closeContentDescription,
                tint = colors.content,
                modifier = Modifier
                    .size(14.dp)
                    .clickable(role = Role.Button, onClick = onCloseClick),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppChipPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        listOf(
            AppChipStyle.Filled to AppChipSize.Medium,
            AppChipStyle.Filled to AppChipSize.Small,
            AppChipStyle.Outline to AppChipSize.Medium,
            AppChipStyle.Filter to AppChipSize.Small,
        ).forEach { (style, size) ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppChip(text = "붕어빵", style = style, size = size, onClick = {})
                AppChip(text = "붕어빵", style = style, size = size, selected = true, onClick = {})
            }
        }
        AppChip(text = "붕어빵", style = AppChipStyle.Filter, selected = true, onClick = {}, onCloseClick = {})
    }
}
