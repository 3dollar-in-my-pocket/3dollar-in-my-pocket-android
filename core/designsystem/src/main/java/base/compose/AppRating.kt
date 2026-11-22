package base.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zion830.threedollars.core.designsystem.R

enum class AppRatingStyle(val backgroundColor: Color) {
    Pink(backgroundColor = Pink100),
    None(backgroundColor = Color.Transparent),
}

const val APP_RATING_MAX = 5

@Composable
fun AppRating(
    value: Int,
    modifier: Modifier = Modifier,
    style: AppRatingStyle = AppRatingStyle.Pink,
    backgroundColor: Color = style.backgroundColor,
    filledColor: Color = Pink,
    emptyColor: Color = Gray70,
    maxValue: Int = APP_RATING_MAX,
) {
    val starCount = maxValue.coerceAtLeast(0)
    val filledCount = value.coerceIn(0, starCount)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(starCount) { index ->
            Image(
                painter = painterResource(R.drawable.ic_star_solid_12),
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (index < filledCount) filledColor else emptyColor),
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppRatingPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        (APP_RATING_MAX downTo 1).forEach { value ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppRating(value = value)
                AppRating(value = value, style = AppRatingStyle.None)
            }
        }
    }
}
