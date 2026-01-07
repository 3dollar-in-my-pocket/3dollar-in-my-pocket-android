package com.zion830.threedollars.ui.storeDetail.boss.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray90
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.threedollar.common.R as CommonR

@Composable
fun VerifiedStoreBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                color = Gray90,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_verified_store_mark),
            modifier = Modifier.size(21.dp),
            contentDescription = "공식 인증 마크"
        )
        Text(
            text = buildAnnotatedString {
                append(stringResource(CommonR.string.store_verified_banner1))

                withStyle(SpanStyle(color = Pink)) {
                    append(stringResource(CommonR.string.store_verified_banner2))
                }

                append(stringResource(CommonR.string.store_verified_banner3))
            },
            fontFamily = PretendardFontFamily,
            fontSize = dpToSp(14),
            fontWeight = FontWeight.W600,
            color = ColorWhite
        )
    }
}