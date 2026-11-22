package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

enum class AppButtonSize(
    val height: Dp,
    val cornerRadius: Dp,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val fontSize: Int,
    val lineHeight: Int,
    val fontWeight: FontWeight,
) {
    Medium(
        height = 48.dp,
        cornerRadius = 12.dp,
        horizontalPadding = 12.dp,
        iconSize = 24.dp,
        fontSize = 14,
        lineHeight = 20,
        fontWeight = FontWeight.SemiBold,
    ),
    Small(
        height = 34.dp,
        cornerRadius = 10.dp,
        horizontalPadding = 10.dp,
        iconSize = 16.dp,
        fontSize = 12,
        lineHeight = 18,
        fontWeight = FontWeight.Bold,
    ),
}

enum class AppButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderColor: Color? = null,
) {
    Primary(backgroundColor = Pink, contentColor = ColorWhite),
    Secondary(backgroundColor = Gray10, contentColor = Gray70),
    Destructive(backgroundColor = Red, contentColor = ColorWhite),
    Outline(backgroundColor = ColorWhite, contentColor = Gray50, borderColor = Gray40),
    Dark(backgroundColor = Gray50, contentColor = ColorWhite),
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: AppButtonSize = AppButtonSize.Medium,
    style: AppButtonStyle = AppButtonStyle.Primary,
    enabled: Boolean = true,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
) {
    val backgroundColor = if (enabled) style.backgroundColor else Gray30
    val contentColor = if (enabled) style.contentColor else ColorWhite
    val borderColor = if (enabled) style.borderColor else null
    val shape = RoundedCornerShape(size.cornerRadius)

    Row(
        modifier = modifier
            .height(size.height)
            .clip(shape)
            .background(backgroundColor)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = size.horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.let {
            Icon(painter = it, contentDescription = null, tint = contentColor, modifier = Modifier.size(size.iconSize))
        }
        Text(
            text = text,
            color = contentColor,
            fontFamily = PretendardFontFamily,
            fontWeight = size.fontWeight,
            fontSize = dpToSp(size.fontSize),
            lineHeight = dpToSp(size.lineHeight),
            letterSpacing = (-0.01).em,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        trailingIcon?.let {
            Icon(painter = it, contentDescription = null, tint = contentColor, modifier = Modifier.size(size.iconSize))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AppButtonStyle.entries.forEach { style ->
            AppButtonSize.entries.forEach { size ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppButton(text = "Label", onClick = {}, size = size, style = style)
                    AppButton(text = "Label", onClick = {}, size = size, style = style, enabled = false)
                }
            }
        }
    }
}
