package base.compose

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun dpToSp(dp: Int): TextUnit {
    val density = LocalDensity.current
    val dpValue = dp.dp
    return (dpValue.value / density.fontScale).sp
}

/**
 * TODO - Need to apply app themes
 */
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme (
        colorScheme = lightColorScheme(),
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = PretendardFontFamily),
            LocalAppShape provides AppShape(),
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = Pink,
                backgroundColor = Pink.copy(alpha = 0.4f)
            ),
            content = content
        )
    }
}
