package com.my.presentation.page.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.my.presentation.R
import com.my.presentation.page.data.MyPageButton
import com.my.presentation.page.data.MyPageSectionTitle
import com.my.presentation.page.data.myPageButtonPreview
import com.my.presentation.page.data.myPageSectionTitlePreview
import zion830.com.common.base.compose.Gray100
import zion830.com.common.base.compose.Gray30
import zion830.com.common.base.compose.Gray50
import zion830.com.common.base.compose.Gray80
import zion830.com.common.base.compose.Pink
import zion830.com.common.base.compose.PretendardFontFamily
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
            Spacer(modifier = Modifier.height(28.dp))
            AsyncImage(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                model = "",
                contentDescription = "내 칭호 사진",
                placeholder = painterResource(id = R.drawable.ic_no_store),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "테스트세트스",
                color = Pink,
                fontSize = dpToSp(dp = 14),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W500,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Gray80)
                    .padding(horizontal = 4.dp, vertical = 3.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "마포구 몽키스패너", fontSize = dpToSp(dp = 30),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MyPageInformationButtons(buttonItems: List<MyPageButton>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        buttonItems.forEachIndexed { index,myPageButton ->
            MyPageInformationButton(
                myPageButton.topText,
                myPageButton.bottomText,
                myPageButton.onClick
            )
            if (index < buttonItems.size - 1) {
                Divider(
                    color = Gray80,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(vertical = 18.dp)
                )
            }
        }
    }
}

@Composable
fun MyPageSectionTitle(myPageSectionTitle: MyPageSectionTitle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = myPageSectionTitle.topIcon),
                contentDescription = myPageSectionTitle.topTitle,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = myPageSectionTitle.topTitle, fontSize = dpToSp(dp = 12),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight(700),
                color = Gray50,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = myPageSectionTitle.bottomTitle, fontSize = dpToSp(dp = 24),
                lineHeight = 31.2.sp,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight(400),
                color = Color.White,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.str_mypage_count, myPageSectionTitle.count),
                    fontSize = dpToSp(dp = 14),
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight(600),
                    color = Pink,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_white_arrow),
                    contentDescription = "화살표",
                    modifier = Modifier.size(12.dp)
                )
            }
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
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = topText, fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W700,
            color = Color.White, fontSize = dpToSp(dp = 16)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = bottomText, fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            color = Gray30, fontSize = dpToSp(dp = 12)
        )
    }
}

@Preview
@Composable
fun MyPageInformationButtonsView(buttonItems: List<MyPageButton> = myPageButtonPreview) {
    MyPageInformationButtons(buttonItems = buttonItems)
}

@Preview
@Composable
fun MyPageInformationTitlesView(titleItems: List<MyPageSectionTitle> = myPageSectionTitlePreview) {
    Column {
        titleItems.forEach { myPageSectionTitle ->
            MyPageSectionTitle(myPageSectionTitle = myPageSectionTitle)
        }
    }
}