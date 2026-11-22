package base.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

/**
 * 피그마 TextField. 제목(선택) + 입력 박스(H44) + 도움말(선택).
 * 포커스되면 main/pink 테두리, [isError]면 main/red 테두리와 빨간 도움말.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    placeholder: String? = null,
    suffix: String? = null,
    helperText: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(8.dp)
    val borderColor = when {
        isError -> Red
        isFocused -> Pink
        else -> Color.Transparent
    }
    val textStyle = TextStyle(
        color = Gray90,
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = dpToSp(14),
        lineHeight = dpToSp(20),
        letterSpacing = (-0.01).em,
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        if (title != null) {
            Text(
                text = title,
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = dpToSp(14),
                lineHeight = dpToSp(20),
                letterSpacing = (-0.01).em,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(shape)
                .background(Gray10)
                .border(1.dp, borderColor, shape),
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = SolidColor(Pink),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder != null) {
                            Text(text = placeholder, style = textStyle.copy(color = Gray50), maxLines = 1)
                        }
                        innerTextField()
                    }
                    if (suffix != null) {
                        Text(text = suffix, style = textStyle.copy(color = Gray70))
                    }
                }
            },
        )
        if (helperText != null) {
            Text(
                text = helperText,
                color = if (isError) Red else Gray50,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(12),
                lineHeight = dpToSp(18),
                letterSpacing = (-0.01).em,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AppTextField(value = "", onValueChange = {}, title = "가게 이름", placeholder = "가게 이름을 입력해 주세요")
        AppTextField(value = "붕어빵 가게", onValueChange = {}, title = "가게 이름")
        AppTextField(
            value = "",
            onValueChange = {},
            title = "가게 이름",
            placeholder = "가게 이름을 입력해 주세요",
            helperText = "가게 이름을 입력해 주세요",
            isError = true,
        )
        AppTextField(value = "1000", onValueChange = {}, suffix = "원", modifier = Modifier.width(160.dp))
    }
}
