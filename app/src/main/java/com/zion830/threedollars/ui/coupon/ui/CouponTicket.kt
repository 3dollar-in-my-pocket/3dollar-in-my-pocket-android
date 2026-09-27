package com.zion830.threedollars.ui.coupon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import base.compose.Gray60
import base.compose.Gray70
import base.compose.Gray80
import base.compose.Gray90
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.domain.store.model.IssuedCouponModel
import com.threedollar.domain.store.model.IssuedCouponStatus
import com.zion830.threedollars.ui.coupon.model.CouponDisplay
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 쿠폰 모양 카드 (iOS `BossStoreCouponView`). 사용 가능 쿠폰은 좌상단에 남은 기간 뱃지를 겹쳐 보여주고,
 * 지난 쿠폰은 회색 카드에 우측 상태 문구(사용완료/기간만료)를 보여준다.
 * [showTrailing]이 false 면 우측 버튼 영역(점선·화살표·상태)을 그리지 않는다(쿠폰 사용 바텀시트).
 */
@Composable
internal fun CouponTicket(
    coupon: IssuedCouponModel,
    modifier: Modifier = Modifier,
    showTrailing: Boolean = true,
) {
    val isAvailable = coupon.status == IssuedCouponStatus.ISSUED
    val remainingDays = if (isAvailable) CouponDisplay.remainingDays(coupon) else null
    val (start, end) = CouponDisplay.periodDates(coupon)
    val textColor = if (isAvailable) Gray80 else Gray60
    val periodColor = if (isAvailable) Gray70 else Gray60

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = if (remainingDays != null) BADGE_OVERLAP else 0.dp)
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .height(IntrinsicSize.Min),
        ) {
            Image(
                painter = painterResource(
                    if (isAvailable) DesignSystemR.drawable.img_coupon_background else DesignSystemR.drawable.img_coupon_background_black,
                ),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 18.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                ) {
                    Text(
                        text = coupon.name,
                        color = textColor,
                        fontFamily = PretendardFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = dpToSp(16),
                        lineHeight = dpToSp(24),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(CommonR.string.my_coupons_period, start, end),
                        color = periodColor,
                        fontFamily = PretendardFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = dpToSp(14),
                    )
                }
                if (showTrailing) {
                    CouponDashedDivider(
                        color = if (isAvailable) COUPON_DIVIDER_PURPLE else Gray70,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 12.dp),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    CouponTrailing(status = coupon.status)
                }
                Spacer(modifier = Modifier.width(25.dp))
            }
        }
        remainingDays?.let { days ->
            Text(
                text = if (days == 0L) {
                    stringResource(CommonR.string.my_coupons_today)
                } else {
                    stringResource(CommonR.string.my_coupons_days_left, days.toInt())
                },
                color = Pink,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(12),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Gray90)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun CouponTrailing(status: IssuedCouponStatus) {
    when (status) {
        IssuedCouponStatus.ISSUED -> Image(
            painter = painterResource(DesignSystemR.drawable.ic_chevron_right_30),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
        )

        IssuedCouponStatus.USED, IssuedCouponStatus.EXPIRED -> Text(
            text = stringResource(
                if (status == IssuedCouponStatus.USED) CommonR.string.my_coupons_status_used else CommonR.string.my_coupons_status_expired,
            ),
            color = Gray60,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(14),
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 30.dp),
        )
    }
}

@Composable
private fun CouponDashedDivider(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.width(1.dp)) {
        drawLine(
            color = color,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = size.width,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 3.dp.toPx())),
        )
    }
}

private val BADGE_OVERLAP = 18.dp

/** 쿠폰 디자인 지정 보라. 디자인 시스템 토큰이 없어 iOS·가게 상세 쿠폰 섹션과 같은 값을 쓴다. */
private val COUPON_DIVIDER_PURPLE = Color(0xFFBC4BD6)
