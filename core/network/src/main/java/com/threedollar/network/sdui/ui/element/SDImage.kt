package com.threedollar.network.sdui.ui.element

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.threedollar.network.sdui.model.element.SDImageModel

@Composable
fun SDImage(
    model: SDImageModel,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = model.url,
            contentDescription = "이미지",
            modifier = Modifier
                .width((model.style?.width ?: 12f).dp)
                .height((model.style?.height ?: 12f).dp),
            contentScale = contentScale
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSDImage() {
    SDImage(
        model = SDImageModel(
            url = "https://via.placeholder.com/150",
            style = SDImageModel.Style(width = 100f, height = 100f)
        )
    )
}
