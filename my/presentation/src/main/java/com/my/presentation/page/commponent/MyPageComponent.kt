package com.my.presentation.page.commponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.my.presentation.page.data.MyPageShop
import com.my.presentation.page.data.myPageShopPreview
import zion830.com.common.base.compose.Gray40
import zion830.com.common.base.compose.PretendardFontFamily
import zion830.com.common.base.compose.dpToSp

@Preview
@Composable
fun MyPageShopInfoView(myPageShop: MyPageShop = myPageShopPreview) {
    Row(
        modifier =  Modifier.fillMaxWidth()
            .height(48.dp)
    ) {
        AsyncImage(
            modifier = Modifier.size(48.dp),
            model = myPageShop.imageUrl,
            contentDescription = "카테고리 이미지"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth()) {
                myPageShop.tags.forEach {
                    Text(
                        text = it, fontSize = dpToSp(dp = 12),
                        fontFamily = PretendardFontFamily,
                        fontWeight = FontWeight.W500,
                        color = Gray40
                    )
                }
            }
            Text(
                text = myPageShop.title, fontSize = dpToSp(dp = 16),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                color = Color.White
            )
        }
    }
}