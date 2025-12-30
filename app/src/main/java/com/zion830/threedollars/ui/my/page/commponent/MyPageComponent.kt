package com.zion830.threedollars.ui.my.page.commponent

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.Gray40
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.zion830.threedollars.ui.my.page.data.MyPageShop
import com.zion830.threedollars.ui.my.page.data.myPageShopPreview

@Preview
@Composable
fun MyPageShopInfoView(myPageShop: MyPageShop = myPageShopPreview) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        AsyncImage(
            modifier = Modifier.size(48.dp),
            model = myPageShop.imageUrl,
            contentDescription = "카테고리 이미지"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = myPageShop.tags.joinToString(" "),
                fontSize = dpToSp(dp = 12),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W500,
                color = Gray40,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = myPageShop.title, fontSize = dpToSp(dp = 16),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}