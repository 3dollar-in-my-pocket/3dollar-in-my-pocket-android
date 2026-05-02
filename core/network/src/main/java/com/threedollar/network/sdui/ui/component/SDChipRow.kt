package com.threedollar.network.sdui.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.Gray50
import com.threedollar.network.sdui.model.element.SDChipModel
import com.threedollar.network.sdui.model.element.SDTextModel
import com.threedollar.network.sdui.ui.element.SDChip

@Composable
fun SDChipRow(
    chips: List<SDChipModel>,
    space: Dp = 4.dp,
    divider: @Composable (() -> Unit) = { SDChipRowDefaultDivider() }
) {
    Row(
        modifier = Modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space)
    ) {
        chips.forEachIndexed { index, it ->
            SDChip(it)

            if (index < chips.lastIndex) {
                divider()
            }
        }
    }
}

@Composable
internal fun SDChipRowDefaultDivider() {
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(1.dp)
            .background(Gray50)
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSDChipRow() {
    SDChipRow(
        chips = listOf(
            SDChipModel(
                image = null,
                text = SDTextModel(
                    text = "칩 1",
                    isHtml = false,
                    fontColor = "#000000"
                )
            ),
            SDChipModel(
                image = null,
                text = SDTextModel(
                    text = "칩 2",
                    isHtml = false,
                    fontColor = "#000000"
                )
            ),
            SDChipModel(
                image = null,
                text = SDTextModel(
                    text = "칩 3",
                    isHtml = false,
                    fontColor = "#000000"
                )
            )
        )
    )
}
