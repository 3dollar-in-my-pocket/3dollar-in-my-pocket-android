package com.zion830.threedollars.ui.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/** 커뮤니티 탭 우측 하단에 고정되는 [이 동네 가게 소식 >] 버튼. 탭하면 우리 동네 소식 화면을 연다. */
@Composable
internal fun CommunityFeedButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Pink)
            .noRippleClickable(onClick = onClick)
            .padding(start = 16.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(CommonR.string.community_feed_button),
            color = ColorWhite,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(16),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    }
}
