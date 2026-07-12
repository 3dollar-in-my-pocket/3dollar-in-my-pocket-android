package com.zion830.threedollars.ui.storeDetail.user.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray20
import base.compose.Gray40
import base.compose.Gray50
import base.compose.Gray60
import base.compose.Gray80
import base.compose.Gray100
import base.compose.Green
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.Red
import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItem
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItemState
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private const val SLIDE_IN_MILLIS = 500
private const val SLIDE_OUT_MILLIS = 300

@Composable
fun StoreDetailDisplayItemOverlay(
    state: StoreDetailDisplayItemState,
    onDisplayed: (StoreDetailDisplayItem) -> Unit,
    onDismiss: () -> Unit,
    onVisitClick: (Boolean) -> Unit,
    onReasonClick: (ReasonModel) -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val item = state.item
    val displayEffectKey = item.displayEffectKey()
    val currentItem = rememberUpdatedState(item)
    val currentOnDisplayed = rememberUpdatedState(onDisplayed)
    val rootInteractionSource = remember { MutableInteractionSource() }
    LaunchedEffect(displayEffectKey, state.isVisible) {
        if (displayEffectKey != null && state.isVisible) {
            kotlinx.coroutines.delay(SLIDE_IN_MILLIS.toLong())
            currentItem.value?.let(currentOnDisplayed.value)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                enabled = item != null && state.isVisible,
                indication = null,
                interactionSource = rootInteractionSource,
            ) {
                onDismiss()
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = item != null && state.isVisible,
            enter = slideInVertically(
                animationSpec = tween(SLIDE_IN_MILLIS),
                initialOffsetY = { it + 40 },
            ) + fadeIn(animationSpec = tween(SLIDE_IN_MILLIS)),
            exit = slideOutVertically(
                animationSpec = tween(SLIDE_OUT_MILLIS),
                targetOffsetY = { it + 40 },
            ) + fadeOut(animationSpec = tween(SLIDE_OUT_MILLIS)),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            when (item) {
                is StoreDetailDisplayItem.VisitInducement -> VisitInducementModal(
                    item = item,
                    onVisitClick = onVisitClick,
                    modifier = Modifier.consumeClicks(),
                )

                is StoreDetailDisplayItem.DisappearanceInquiry -> DisappearanceInquiryModal(
                    item = item,
                    onReasonClick = onReasonClick,
                    onReportClick = onReportClick,
                    modifier = Modifier.consumeClicks(),
                )

                null -> Unit
            }
        }
    }
}

@Composable
private fun VisitInducementModal(
    item: StoreDetailDisplayItem.VisitInducement,
    onVisitClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    DisplayItemCard(modifier = modifier) {
        Text(
            text = stringResource(CommonR.string.visit_inducement_modal_title),
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(CommonR.string.visit_inducement_modal_subtitle),
            color = Gray60,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            VisitChoiceButton(
                title = stringResource(CommonR.string.visit_inducement_modal_closed),
                iconResId = DesignSystemR.drawable.img_fail,
                titleColor = Pink,
                enabled = !item.isSubmitting,
                onClick = { onVisitClick(false) },
                modifier = Modifier.weight(1f),
            )
            VisitChoiceButton(
                title = stringResource(CommonR.string.visit_inducement_modal_opened),
                iconResId = DesignSystemR.drawable.img_success,
                titleColor = Green,
                enabled = !item.isSubmitting,
                onClick = { onVisitClick(true) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun VisitChoiceButton(
    title: String,
    iconResId: Int,
    titleColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(84.dp)
            .background(Gray0, RoundedCornerShape(12.dp))
            .border(1.dp, Gray20, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
        Text(
            text = title,
            color = titleColor,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun DisappearanceInquiryModal(
    item: StoreDetailDisplayItem.DisappearanceInquiry,
    onReasonClick: (ReasonModel) -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DisplayItemCard(modifier = modifier) {
        Text(
            text = stringResource(CommonR.string.disappearance_inquiry_modal_title),
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = reportDescriptionText(),
            color = Gray60,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        )

        if (item.isReasonLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(top = 16.dp),
            ) {
                CircularProgressIndicator(
                    color = Red,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp),
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                item.reasons.forEach { reason ->
                    ReasonRow(
                        reason = reason,
                        selected = item.selectedReason == reason,
                        enabled = !item.isSubmitting,
                        onClick = { onReasonClick(reason) },
                    )
                }
            }
        }

        Button(
            onClick = onReportClick,
            enabled = item.selectedReason != null && !item.isSubmitting,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Red,
                contentColor = ColorWhite,
                disabledBackgroundColor = Gray20,
                disabledContentColor = Gray50,
            ),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(48.dp),
        ) {
            Text(
                text = stringResource(CommonR.string.request_delete),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun ReasonRow(
    reason: ReasonModel,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(ColorWhite, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (selected) Red else Gray40,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = reason.description,
            color = if (selected) Gray100 else Gray80,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_check_red_20),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun DisplayItemCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .background(ColorWhite, RoundedCornerShape(20.dp))
            .border(1.dp, Gray20, RoundedCornerShape(20.dp))
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

@Composable
private fun reportDescriptionText() = buildAnnotatedString {
    val text = stringResource(CommonR.string.request_delete_description)
    val keyword = "3건 이상"
    val start = text.indexOf(keyword)
    if (start < 0) {
        append(text)
        return@buildAnnotatedString
    }
    append(text.substring(0, start))
    withStyle(SpanStyle(color = Gray80, fontWeight = FontWeight.W700)) {
        append(keyword)
    }
    append(text.substring(start + keyword.length))
}

@Composable
private fun Modifier.consumeClicks(): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return clickable(
        interactionSource = interactionSource,
        indication = null,
    ) {}
}

@Preview(
    name = "Visit inducement",
    widthDp = 360,
    heightDp = 240,
    showBackground = true,
    backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun StoreDetailDisplayItemOverlayVisitPreview() {
    StoreDetailDisplayItemOverlay(
        state = StoreDetailDisplayItemState(
            item = StoreDetailDisplayItem.VisitInducement(
                storeId = 1,
                trigger = null,
            ),
            isVisible = true,
        ),
        onDisplayed = {},
        onDismiss = {},
        onVisitClick = {},
        onReasonClick = {},
        onReportClick = {},
    )
}

@Preview(
    name = "Disappearance inquiry loading",
    widthDp = 360,
    heightDp = 300,
    showBackground = true,
    backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun StoreDetailDisplayItemOverlayReasonLoadingPreview() {
    StoreDetailDisplayItemOverlay(
        state = StoreDetailDisplayItemState(
            item = StoreDetailDisplayItem.DisappearanceInquiry(
                storeId = 1,
                trigger = null,
            ),
            isVisible = true,
        ),
        onDisplayed = {},
        onDismiss = {},
        onVisitClick = {},
        onReasonClick = {},
        onReportClick = {},
    )
}

@Preview(
    name = "Disappearance inquiry selected",
    widthDp = 360,
    heightDp = 420,
    showBackground = true,
    backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun StoreDetailDisplayItemOverlayReasonSelectedPreview() {
    val reasons = previewReasons()
    StoreDetailDisplayItemOverlay(
        state = StoreDetailDisplayItemState(
            item = StoreDetailDisplayItem.DisappearanceInquiry(
                storeId = 1,
                trigger = null,
                reasons = reasons,
                selectedReason = reasons.first(),
                isReasonLoading = false,
            ),
            isVisible = true,
        ),
        onDisplayed = {},
        onDismiss = {},
        onVisitClick = {},
        onReasonClick = {},
        onReportClick = {},
    )
}

private fun previewReasons() = listOf(
    ReasonModel(
        description = "가게가 사라졌어요",
        type = "STORE_CLOSED",
    ),
    ReasonModel(
        description = "운영하지 않는 것 같아요",
        type = "NOT_OPERATING",
    ),
    ReasonModel(
        description = "잘못 등록된 가게예요",
        type = "INVALID_STORE",
    ),
)

internal data class StoreDetailDisplayItemEffectKey(
    val storeId: Int,
    val itemType: StoreDisplayItemType,
)

internal fun StoreDetailDisplayItem?.displayEffectKey(): StoreDetailDisplayItemEffectKey? =
    this?.let { item ->
        StoreDetailDisplayItemEffectKey(
            storeId = item.storeId,
            itemType = item.itemType,
        )
    }
