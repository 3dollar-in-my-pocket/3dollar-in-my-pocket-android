package base.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppProgressBar(
    step: Int,
    modifier: Modifier = Modifier,
    stepCount: Int = 4,
) {
    val count = stepCount.coerceAtLeast(1)
    val fraction by animateFloatAsState(
        targetValue = step.coerceIn(0, count).toFloat() / count,
        label = "AppProgressBar",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Gray30),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(Pink),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppProgressBarPreview() {
    Column {
        (1..4).forEach { step ->
            AppProgressBar(step = step)
            Spacer(modifier = Modifier.padding(4.dp))
        }
    }
}
