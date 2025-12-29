package base.compose

import androidx.compose.runtime.Composable
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