package com.my.presentation.page.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.my.presentation.R
import com.my.presentation.page.data.MyPageButton
import com.my.presentation.page.data.myPageButtonPreview
import zion830.com.common.base.compose.AppleGothicFontFamily
import zion830.com.common.base.compose.ColorSubRed
import zion830.com.common.base.compose.Gray100
import zion830.com.common.base.compose.Gray90
import zion830.com.common.base.compose.dpToSp

@Composable
fun MyPageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(Gray100)
    ) {

    }
}

@Preview
@Composable
fun MyPageTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Text(
            text = "마이페이지", style = TextStyle(
                fontSize = dpToSp(dp = 16),
                lineHeight = 24.sp,
                color = Color.White
            ), modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = { }, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "마이페이지 셋팅 아이콘"
            )
        }
    }
}

@Preview
@Composable
fun MyPageUserInformation() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.img_back_gray),
            contentDescription = "내 정보 배경"
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(modifier = Modifier.height(11.dp))
            AsyncImage(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                model = "",
                contentDescription = "내 칭호 사진",
                placeholder = painterResource(id = R.drawable.ic_no_store),
                contentScale = ContentScale.Crop,
            )
            Divider(modifier = Modifier.height(8.dp))
            Text(
                text = "테스트세트스",
                color = ColorSubRed,
                fontSize = dpToSp(dp = 14),
                fontFamily = AppleGothicFontFamily,
                fontWeight = FontWeight.W500,
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .border(1.dp, ColorSubRed, RoundedCornerShape(50.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
            Divider(modifier = Modifier.height(8.dp))
            Text(
                text = "마포구 몽키스패너", fontSize = dpToSp(dp = 30),
                fontFamily = AppleGothicFontFamily,
                fontWeight = FontWeight.W700,
                color = Color.White
            )
            Divider(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MyPageInformationButtons(buttonItems: List<MyPageButton>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 36.dp, end = 36.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
    ) {
        buttonItems.forEach { myPageButton ->
            MyPageInformationButton(
                myPageButton.topText,
                myPageButton.bottomText,
                myPageButton.onClick
            )
        }
    }
}

@Preview
@Composable
fun MyPageInformationButton(
    topText: String = "상단",
    bottomText: String = "하단",
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Gray90)
            .padding(bottom = 12.dp, start = 10.dp, end = 10.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = topText, fontFamily = AppleGothicFontFamily,
            fontWeight = FontWeight.W700,
            color = Color.White, fontSize = dpToSp(dp = 24)
        )
        Divider(modifier = Modifier.height(14.dp))
        Text(
            text = bottomText, fontFamily = AppleGothicFontFamily,
            fontWeight = FontWeight.W400,
            color = Color.White, fontSize = dpToSp(dp = 12)
        )
    }
}

@Preview
@Composable
fun MyPageInformationButtonsView(buttonItems: List<MyPageButton> = myPageButtonPreview){
    MyPageInformationButtons(buttonItems = buttonItems)
}