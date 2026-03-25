package com.threedollar.network.sdui.ui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.threedollar.network.sdui.model.element.SDChipModel
import com.threedollar.network.sdui.model.element.SDImageModel
import com.threedollar.network.sdui.model.element.SDTextModel


@Composable
fun SDChip(
    model: SDChipModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        model.image?.let {
            SDImage(it)
        }
        model.text?.let {
            SDText(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSDChipTextOnly() {
    SDChip(
        model = SDChipModel(
            image = null,
            text = SDTextModel(
                text = "칩 텍스트",
                isHtml = false,
                fontColor = "#000000"
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSDChipWithImage() {
    SDChip(
        model = SDChipModel(
            image = SDImageModel(
                url = null,
                style = SDImageModel.Style(width = 16f, height = 16f)
            ),
            text = SDTextModel(
                text = "이미지 칩",
                isHtml = false,
                fontColor = "#333333"
            )
        )
    )
}
