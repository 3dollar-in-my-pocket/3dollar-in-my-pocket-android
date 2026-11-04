package com.zion830.threedollars.ui.review

import android.view.ViewTreeObserver
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import base.compose.ColorB7B7B7
import base.compose.ColorF9737E
import base.compose.ColorFf858F
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray20
import base.compose.Gray30
import base.compose.Gray50
import base.compose.Gray70
import base.compose.PretendardFontFamily
import coil3.compose.AsyncImage
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.threedollar.common.R as CommonR

private val ContentShape = RoundedCornerShape(12.dp)
private val PhotoSize = 72.dp

@Composable
fun ReviewWriteScreen(
    state: ReviewWriteState,
    onClose: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onContentsChange: (String) -> Unit,
    onAddPhotoClick: () -> Unit,
    onRemovePhoto: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp),
    ) {
        ReviewWriteHeader(onClose = onClose)
        Spacer(modifier = Modifier.height(28.dp))
        ReviewRating(storeName = state.storeName, rating = state.rating, onRatingChange = onRatingChange)
        Spacer(modifier = Modifier.height(28.dp))
        if (state.photos.isEmpty()) {
            AttachPhotoButton(onClick = onAddPhotoClick)
        } else {
            ReviewPhotoRow(state = state, onAddPhotoClick = onAddPhotoClick, onRemovePhoto = onRemovePhoto)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ReviewContentsField(contents = state.contents, onContentsChange = onContentsChange)
        Spacer(modifier = Modifier.height(28.dp))
        SubmitButton(enabled = state.canSubmit, onClick = onSubmit)
    }
}

@Composable
private fun ReviewWriteHeader(onClose: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(CommonR.string.review_write_title),
            modifier = Modifier.weight(1f),
            style = pretendard(size = 20, weight = FontWeight.SemiBold, lineHeight = 28),
            color = Gray100,
        )
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_close_circle_gray40_30),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable(onClick = onClose),
        )
    }
}

@Composable
private fun ReviewRating(storeName: String, rating: Int, onRatingChange: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (storeName.isNotBlank()) {
            Text(
                text = storeName,
                style = pretendard(size = 16, weight = FontWeight.SemiBold, lineHeight = 24),
                color = Gray100,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..ReviewWriteState.MAX_RATING).forEach { star ->
                val description = stringResource(CommonR.string.review_write_rating_description, star)
                Image(
                    painter = painterResource(
                        if (star <= rating) DesignSystemR.drawable.ic_review_star_on_40 else DesignSystemR.drawable.ic_review_star_off_40,
                    ),
                    contentDescription = description,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onRatingChange(star) },
                )
            }
        }
    }
}

@Composable
private fun AttachPhotoButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(ContentShape)
            .border(1.dp, Gray20, ContentShape)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = painterResource(DesignSystemR.drawable.ic_image_add_20), contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(CommonR.string.review_write_attach_photo),
            style = pretendard(size = 14, weight = FontWeight.SemiBold, lineHeight = 20),
            color = Gray70,
        )
    }
}

@Composable
private fun ReviewPhotoRow(state: ReviewWriteState, onAddPhotoClick: () -> Unit, onRemovePhoto: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), contentPadding = PaddingValues(0.dp)) {
        item(key = ADD_PHOTO_KEY) {
            AddPhotoTile(count = state.photos.size, enabled = state.canAddPhoto, onClick = onAddPhotoClick)
        }
        items(state.photos, key = { it.key }) { photo ->
            PhotoTile(photo = photo, onRemove = { onRemovePhoto(photo.key) })
        }
    }
}

@Composable
private fun AddPhotoTile(count: Int, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .size(PhotoSize)
            .clip(shape)
            .background(Gray10)
            .border(BorderStroke(1.dp, Gray20), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(top = 12.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_photo_add_circle_22),
            contentDescription = stringResource(CommonR.string.review_write_photo_add_description),
            modifier = Modifier.size(24.dp).padding(1.dp),
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = ColorFf858F, fontWeight = FontWeight.Bold)) { append(count.toString()) }
                append("/${ReviewWriteState.MAX_PHOTO_COUNT}")
            },
            style = pretendard(size = 16, weight = FontWeight.Normal, lineHeight = 24),
            color = ColorB7B7B7,
        )
    }
}

@Composable
private fun PhotoTile(photo: ReviewPhoto, onRemove: () -> Unit) {
    Box(modifier = Modifier.size(PhotoSize)) {
        AsyncImage(
            model = when (photo) {
                is ReviewPhoto.Uploaded -> photo.image.imageUrl
                is ReviewPhoto.Picked -> photo.uri
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(PhotoSize)
                .clip(ContentShape)
                .background(Gray10),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(DesignSystemR.drawable.ic_photo_delete_18),
                contentDescription = stringResource(CommonR.string.review_write_photo_delete_description),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun ReviewContentsField(contents: String, onContentsChange: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    ClearFocusWhenKeyboardHides()
    val textStyle = pretendard(size = 14, weight = FontWeight.Normal, lineHeight = 20)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        BasicTextField(
            value = contents,
            onValueChange = { onContentsChange(it.take(ReviewWriteState.MAX_CONTENTS_LENGTH)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(ContentShape)
                .background(Gray0)
                .border(1.dp, if (isFocused) ColorFf858F else Gray20, ContentShape)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            textStyle = textStyle.copy(color = Gray70),
            cursorBrush = SolidColor(ColorFf858F),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Box {
                    if (contents.isEmpty()) {
                        Text(text = stringResource(CommonR.string.review_write_hint), style = textStyle, color = ColorB7B7B7)
                    }
                    innerTextField()
                }
            },
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(CommonR.string.review_write_length_guide),
                modifier = Modifier.weight(1f),
                style = pretendard(size = 12, weight = FontWeight.Normal, lineHeight = 18),
                color = Gray70,
            )
            Text(
                text = buildAnnotatedString {
                    if (contents.isEmpty()) {
                        append("0")
                    } else {
                        withStyle(SpanStyle(color = ColorF9737E, fontWeight = FontWeight.Medium)) { append(contents.length.toString()) }
                    }
                    append("/${ReviewWriteState.MAX_CONTENTS_LENGTH}")
                },
                style = pretendard(size = 12, weight = FontWeight.Normal, lineHeight = 18),
                color = Gray50,
                textAlign = TextAlign.End,
            )
        }
    }
}

/** 시트(다이얼로그 창)에서는 Compose ime 인셋이 오지 않아, 루트 인셋으로 키보드가 내려가는 순간을 잡아 입력 포커스를 푼다. */
@Composable
private fun ClearFocusWhenKeyboardHides() {
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    DisposableEffect(view) {
        var wasImeVisible = false
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isImeVisible = ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime()) == true
            if (wasImeVisible && !isImeVisible) focusManager.clearFocus()
            wasImeVisible = isImeVisible
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(listener) }
    }
}

@Composable
private fun SubmitButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(ContentShape)
            .background(if (enabled) ColorFf858F else Gray30)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(CommonR.string.review_write_submit),
            style = pretendard(size = 14, weight = FontWeight.SemiBold, lineHeight = 20),
            color = ColorWhite,
        )
    }
}

private fun pretendard(size: Int, weight: FontWeight, lineHeight: Int) = TextStyle(
    fontFamily = PretendardFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = (-0.01 * size).sp,
)

private const val ADD_PHOTO_KEY = "add_photo"

@Preview(showBackground = true)
@Composable
private fun ReviewWriteScreenEmptyPreview() {
    ReviewWriteScreen(
        state = ReviewWriteState(storeName = "킹 잉어빵"),
        onClose = {}, onRatingChange = {}, onContentsChange = {}, onAddPhotoClick = {}, onRemovePhoto = {}, onSubmit = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ReviewWriteScreenFilledPreview() {
    ReviewWriteScreen(
        state = ReviewWriteState(
            storeName = "킹 잉어빵",
            rating = 4,
            contents = "저희 집은 1번 출구인데 일부러 4번출구에서 붕어빵 사들고 가요.",
            photos = listOf(ReviewPhoto.Picked("a"), ReviewPhoto.Picked("b")),
        ),
        onClose = {}, onRatingChange = {}, onContentsChange = {}, onAddPhotoClick = {}, onRemovePhoto = {}, onSubmit = {},
    )
}
