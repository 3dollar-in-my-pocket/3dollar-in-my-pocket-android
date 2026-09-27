package com.zion830.threedollars.ui.coupon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import base.compose.ColorWhite
import base.compose.Gray100
import base.compose.Gray50
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.Red
import base.compose.dpToSp
import com.threedollar.domain.store.model.IssuedCouponModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 쿠폰 사용 바텀시트. 사장님께 쿠폰을 보여주고 [사용하기]를 누르면 확인 얼럿을 띄운다.
 * 사용 요청 중에는 로딩을 덮어 중복 요청과 닫기를 막는다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CouponUseBottomSheet(
    coupon: IssuedCouponModel,
    isUsing: Boolean,
    onDismiss: () -> Unit,
    onUseClick: () -> Unit,
) {
    val currentIsUsing by rememberUpdatedState(isUsing)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { !currentIsUsing },
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Gray100,
        dragHandle = null,
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 20.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = sheetTitle(),
                        modifier = Modifier.weight(1f),
                        color = ColorWhite,
                        fontFamily = PretendardFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = dpToSp(24),
                        lineHeight = dpToSp(32),
                    )
                    Image(
                        painter = painterResource(DesignSystemR.drawable.ic_close_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(ColorWhite),
                        modifier = Modifier
                            .size(24.dp)
                            .noRippleClickable(enabled = !isUsing, onClick = onDismiss),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(CommonR.string.coupon_use_sheet_description),
                    color = Gray50,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = dpToSp(14),
                )
                Spacer(modifier = Modifier.height(24.dp))
                CouponTicket(coupon = coupon, showTrailing = false)
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Pink)
                        .noRippleClickable(enabled = !isUsing, onClick = onUseClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(CommonR.string.store_detail_coupon_use),
                        color = ColorWhite,
                        fontFamily = PretendardFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = dpToSp(16),
                    )
                }
            }
            if (isUsing) {
                CircularProgressIndicator(color = Pink, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun sheetTitle() = run {
    val title = stringResource(CommonR.string.coupon_use_sheet_title)
    val emphasis = stringResource(CommonR.string.coupon_use_sheet_title_emphasis)
    buildAnnotatedString {
        val start = title.indexOf(emphasis)
        if (start < 0) {
            append(title)
        } else {
            append(title.substring(0, start))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(emphasis) }
            append(title.substring(start + emphasis.length))
        }
    }
}

/** 쿠폰 사용 확인 얼럿. 한 번 쓰면 되돌릴 수 없어서 한 번 더 묻는다. */
@Composable
internal fun CouponUseConfirmDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(ColorWhite)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(CommonR.string.coupon_use_confirm_message),
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = dpToSp(20),
                lineHeight = dpToSp(28),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ConfirmButton(
                    text = stringResource(CommonR.string.cancel),
                    background = Gray50,
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                )
                ConfirmButton(
                    text = stringResource(CommonR.string.store_detail_coupon_use),
                    background = Red,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ConfirmButton(
    text: String,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = ColorWhite,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(14),
        )
    }
}
