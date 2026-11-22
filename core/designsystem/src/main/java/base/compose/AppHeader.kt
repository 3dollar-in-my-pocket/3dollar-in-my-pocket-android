package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Composable
fun AppHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    description: String? = null,
) {
    AppHeader(
        title = title,
        annotatedSubtitle = subtitle?.let { AnnotatedString(it) },
        description = description,
        modifier = modifier,
    )
}

@Composable
fun AppHeader(
    title: String,
    annotatedSubtitle: AnnotatedString?,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f, fill = false),
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(24),
                lineHeight = dpToSp(32),
                letterSpacing = (-0.01).em,
            )
            if (!annotatedSubtitle.isNullOrEmpty()) {
                Text(
                    text = annotatedSubtitle,
                    color = Gray40,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = dpToSp(16),
                    lineHeight = dpToSp(24),
                    letterSpacing = (-0.01).em,
                    maxLines = 1,
                )
            }
        }
        if (!description.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = Gray80,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = dpToSp(14),
                letterSpacing = (-0.01).em,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppHeaderPreview() {
    Column {
        AppHeader(title = "가게 정보 입력", description = "지금 당장 가게 이름을 알 수 없다면, 근처의 랜드마크와 함께 입력해 보세요.")
        AppHeader(title = "메뉴 상세 정보 추가", subtitle = "(선택)")
        AppHeader(
            title = "음식 카테고리 선택",
            annotatedSubtitle = buildAnnotatedString {
                append("(")
                withStyle(SpanStyle(color = Pink)) { append("3") }
                append("/10)")
            },
        )
    }
}
