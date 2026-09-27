package com.zion830.threedollars.ui.my.page.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray90
import base.compose.Gray95
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.domain.store.model.IssuedCouponModel
import com.threedollar.domain.store.model.IssuedCouponPageModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.ui.coupon.model.CouponDisplay
import com.zion830.threedollars.ui.my.page.commponent.MyPageShopInfoView
import com.zion830.threedollars.ui.my.page.data.MyPageSectionTitleData
import com.zion830.threedollars.ui.my.page.data.toMyPageShop
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 마이페이지 쿠폰 섹션. 사용 가능한 쿠폰을 가로 카드로 보여주고, 카드·헤더 개수를 탭하면 내 쿠폰함으로 간다.
 */
@Composable
internal fun MyPageCouponSection(
    page: IssuedCouponPageModel,
    onHeaderClick: () -> Unit,
    onCouponClick: (IssuedCouponModel) -> Unit,
) {
    val count = CouponDisplay.sectionCount(page)
    MyPageSectionTitle(
        MyPageSectionTitleData(
            topTitle = stringResource(CommonR.string.my_coupon_section_title),
            topIcon = DesignSystemR.drawable.ic_coupon_solid_16,
            bottomTitle = stringResource(CommonR.string.my_coupon_section_bottom_title),
            countText = when {
                count.count == 0 -> null
                count.hasMore -> stringResource(CommonR.string.my_coupon_section_count_more, count.count)
                else -> stringResource(CommonR.string.str_mypage_count, count.count)
            },
            onClick = onHeaderClick,
        )
    )
    Spacer(modifier = Modifier.height(16.dp))
    if (page.coupons.isEmpty()) {
        MyPageEmptyView(
            DesignSystemR.drawable.img_empty,
            stringResource(CommonR.string.my_coupon_section_empty_title),
            stringResource(CommonR.string.my_coupon_section_empty_message),
        )
        return
    }
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.width(8.dp))
        page.coupons.forEach { coupon ->
            MyPageCouponCard(coupon = coupon, onClick = { onCouponClick(coupon) })
        }
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun MyPageCouponCard(
    coupon: IssuedCouponModel,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Gray95)
            .noRippleClickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Gray90)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(DesignSystemR.drawable.ic_coupon_solid_16),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = coupon.name,
                color = ColorWhite,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(12),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        MyPageShopInfoView(myPageShop = coupon.toMyPageShop())
    }
}
