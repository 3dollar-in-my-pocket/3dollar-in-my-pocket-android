package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.zion830.threedollars.core.designsystem.R

enum class AppTopTheme(val background: Color, val content: Color) {
    Light(background = ColorWhite, content = Gray100),
    Dark(background = Gray100, content = ColorWhite),
}

private val LocalAppTopContentColor = staticCompositionLocalOf { Gray100 }

@Composable
fun AppTop(
    title: String?,
    modifier: Modifier = Modifier,
    theme: AppTopTheme = AppTopTheme.Light,
    onBackClick: (() -> Unit)? = null,
    backContentDescription: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CompositionLocalProvider(LocalAppTopContentColor provides theme.content) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(theme.background),
        ) {
            if (onBackClick != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(56.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBackClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = backContentDescription,
                        tint = theme.content,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 72.dp),
                    color = theme.content,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = dpToSp(16),
                    lineHeight = dpToSp(24),
                    letterSpacing = (-0.01).em,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }
    }
}

@Composable
fun AppTopIcon(
    painter: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalAppTopContentColor.current,
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(24.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    )
}

@Preview(showBackground = true)
@Composable
private fun AppTopPreview() {
    Column {
        AppTop(title = "가게 제보", onBackClick = {}) {
            AppTopIcon(painter = painterResource(R.drawable.ic_close_gray100_24), contentDescription = null, onClick = {})
        }
        AppTop(title = "내 쿠폰함", theme = AppTopTheme.Dark, onBackClick = {})
    }
}
