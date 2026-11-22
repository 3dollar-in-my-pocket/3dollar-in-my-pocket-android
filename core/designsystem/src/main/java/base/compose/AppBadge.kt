package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

enum class AppBadgeStyle(
    val backgroundColor: Color,
    val contentColor: Color,
) {
    Pink(backgroundColor = Pink100, contentColor = base.compose.Pink),
    Gray(backgroundColor = Gray10, contentColor = Gray80),
}

@Composable
fun AppBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: AppBadgeStyle = AppBadgeStyle.Pink,
    backgroundColor: Color = style.backgroundColor,
    leadingIcon: (@Composable (Modifier) -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.invoke(Modifier.size(16.dp))
        Text(
            text = text,
            color = style.contentColor,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(10),
            lineHeight = dpToSp(14),
            letterSpacing = (-0.01).em,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppBadgePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppBadge(text = "붕어빵 전문가")
        AppBadge(text = "작성자", style = AppBadgeStyle.Gray)
        AppBadge(text = "붕어빵 전문가", backgroundColor = Gray80)
    }
}
