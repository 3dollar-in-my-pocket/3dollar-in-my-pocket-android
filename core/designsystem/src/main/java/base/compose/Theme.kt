package base.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun dpToSp(dp: Int) = with(LocalDensity.current) { dp.dp.toSp() }