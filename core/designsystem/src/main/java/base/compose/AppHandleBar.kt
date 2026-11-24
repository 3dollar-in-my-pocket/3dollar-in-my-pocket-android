package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

object AppHandleBarDefaults {
    val Height = 20.dp
}

@Composable
fun AppHandleBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppHandleBarDefaults.Height),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(width = 40.dp, height = 5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(Gray20),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppHandleBarPreview() {
    AppHandleBar()
}
