package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.zion830.threedollars.core.designsystem.R

/**
 * 피그마 SearchField. 왼쪽 아이콘 + 입력 + 지우기 버튼(입력값이 있을 때만).
 */
@Composable
fun AppSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: Painter? = painterResource(R.drawable.ic_reading_glasses),
    onClear: (() -> Unit)? = { onValueChange("") },
    clearContentDescription: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val shape = RoundedCornerShape(12.dp)
    val textStyle = TextStyle(
        color = Gray100,
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = dpToSp(16),
        lineHeight = dpToSp(24),
        letterSpacing = (-0.01).em,
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(ColorWhite)
            .border(1.dp, Gray30, shape),
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(Pink),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (leadingIcon != null) {
                    Icon(
                        painter = leadingIcon,
                        contentDescription = null,
                        tint = Gray100,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(text = placeholder, style = textStyle.copy(color = Gray40), maxLines = 1)
                    }
                    innerTextField()
                }
                if (onClear != null && value.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = onClear),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close_gray100_24),
                            contentDescription = clearContentDescription,
                            tint = Gray100,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun AppSearchFieldPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppSearchField(value = "", onValueChange = {}, placeholder = "주소를 검색해 주세요")
        AppSearchField(value = "강남역", onValueChange = {})
    }
}
