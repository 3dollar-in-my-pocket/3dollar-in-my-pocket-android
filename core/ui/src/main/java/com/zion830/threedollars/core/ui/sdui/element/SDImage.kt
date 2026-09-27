package com.zion830.threedollars.core.ui.sdui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.threedollar.common.sdui.model.element.SDImageModel

private const val DEFAULT_IMAGE_SIZE = 12f
private const val DIMMED_ALPHA = 0.5f

/**
 * 서버 이미지. [modifier]에 크기를 주지 않으면 서버 `style.width/height` 로 그린다.
 * `style.dimmed` 면 검은 반투명 막을 덮고, 그 위에 [overlay]를 그린다.
 */
@Composable
fun SDImage(
    model: SDImageModel,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier,
    sizeFromStyle: Boolean = true,
    overlay: @Composable (() -> Unit)? = null
) {
    val sizeModifier = if (sizeFromStyle) {
        Modifier.size(
            width = (model.style?.width ?: DEFAULT_IMAGE_SIZE).dp,
            height = (model.style?.height ?: DEFAULT_IMAGE_SIZE).dp
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier.then(sizeModifier),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = model.url,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = contentScale
        )
        if (model.style?.dimmed == true) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = DIMMED_ALPHA))
            )
        }
        overlay?.invoke()
    }
}
