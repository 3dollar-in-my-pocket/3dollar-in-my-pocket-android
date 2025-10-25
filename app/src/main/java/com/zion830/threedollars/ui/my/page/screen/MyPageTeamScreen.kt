package com.zion830.threedollars.ui.my.page.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.ui.my.page.data.TeamRow
import zion830.com.common.base.compose.Gray40
import zion830.com.common.base.compose.Pink
import zion830.com.common.base.compose.Pink200
import zion830.com.common.base.compose.Pink500
import zion830.com.common.base.compose.PretendardFontFamily
import zion830.com.common.base.compose.dpToSp

@Composable
fun MyPageTeamScreen(clickBack: () -> Unit, clickAd: () -> Unit = {}, clickTeam: () -> Unit = {}) {
    val teams = listOf(
        TeamRow(role = "Design", members = listOf("이윤이", "박은지")),
        TeamRow(role = "iOS", members = listOf("유현식", "김하경")),
        TeamRow(role = "Android", members = listOf("김민호", "정진용")),
        TeamRow(role = "Backend", members = listOf("강승호", "고예림")),
        TeamRow(role = "Marketer", members = listOf("윤다영", "이한나")),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Pink),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MyPageTeamTitle { clickBack() }
        Image(
            painter = painterResource(id = zion830.com.common.R.drawable.ic_3dollars_logo),
            contentDescription = "로고",
            modifier = Modifier.padding(top = 66.dp, bottom = 40.dp, start = 47.dp, end = 47.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 47.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(teams) {
                MyPageTeamMember(it.role, it.members)
            }
            item {
                AdButton {
                    clickAd()
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { clickTeam() }) {
            Image(painter = painterResource(id = zion830.com.common.R.drawable.ic_instagram), contentDescription = "instagrem")
            Text(
                text = "가슴속 3천원 인스타그램 바로가기",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W500,
                color = Color.White,
                fontSize = dpToSp(dp = 12)
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun MyPageTeamTitle(clickBack: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Image(
            painter = painterResource(id = zion830.com.common.R.drawable.ic_back_white),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { clickBack }
                .padding(end = 16.dp)
        )
        Text(
            text = stringResource(id = CommonR.string.str_team_title), fontSize = dpToSp(dp = 16),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W400,
            color = Color.White, modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun MyPageTeamMember(role: String = "BackEnd", members: List<String> = listOf("강승호", "고예림")) {
    Row(
        modifier = Modifier
            .background(Pink500, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = role,
                fontSize = dpToSp(dp = 10),
                color = Pink200,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                members.forEach {
                    Text(
                        text = it,
                        fontSize = dpToSp(dp = 16),
                        color = Color.White,
                        fontFamily = PretendardFontFamily,
                        fontWeight = FontWeight.W700,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AdButton(clickAd: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .clickable { clickAd() }
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "커피 ☕️ 사주기",
                fontSize = dpToSp(dp = 10),
                color = Pink,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "광고 보기",
                    fontSize = dpToSp(dp = 16),
                    color = Color.Black,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.W700,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Image(
                    painter = painterResource(id = zion830.com.common.R.drawable.ic_white_arrow),
                    contentDescription = "",
                    modifier = Modifier
                        .size(12.dp)
                        .padding(bottom = 25.dp),
                    colorFilter = ColorFilter.tint(Gray40)
                )
            }
        }
    }
}