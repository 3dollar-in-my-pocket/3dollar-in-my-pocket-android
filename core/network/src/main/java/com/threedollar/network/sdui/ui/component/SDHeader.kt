package com.threedollar.network.sdui.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.threedollar.network.sdui.model.component.SDHeaderModel
import com.threedollar.network.sdui.model.element.SDTextModel
import com.threedollar.network.sdui.ui.element.SDText

@Composable
fun SDHeader(
    model: SDHeaderModel,
    modifier: Modifier = Modifier
) {
    model.title?.let {
        SDText(
            model = it,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSDHeader() {
    SDHeader(
        model = SDHeaderModel(
            title = SDTextModel(
                text = "헤더 타이틀",
                isHtml = false,
                fontColor = "#000000"
            )
        )
    )
}
