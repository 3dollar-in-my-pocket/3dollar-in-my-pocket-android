package com.my.presentation.page.screen

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.presentation.page.data.TeamRow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import zion830.com.common.base.compose.Gray40
import zion830.com.common.base.compose.Pink
import zion830.com.common.base.compose.Pink200
import zion830.com.common.base.compose.Pink500
import zion830.com.common.base.compose.PretendardFontFamily
import zion830.com.common.base.compose.dpToSp
import com.threedollar.common.R as CommonR

@Composable
fun MyPageTeamScreen(
    clickBack: () -> Unit,
    clickAd: () -> Unit = {},
    clickTeam: () -> Unit = {}
) {
    val teams = remember {
        persistentListOf(
            TeamRow(role = "Design", members = persistentListOf("이윤이", "박은지")),
            TeamRow(role = "iOS", members = persistentListOf("유현식", "김하경")),
            TeamRow(role = "Android", members = persistentListOf("정진용", "전두영")),
            TeamRow(role = "Backend", members = persistentListOf("강승호", "고예림")),
            TeamRow(role = "Marketer", members = persistentListOf("윤다영", "이한나")),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                clickBack = clickBack
            )
        },
        bottomBar = {
            Footer(
                onClick = clickTeam
            )
        },
        containerColor = Pink
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = zion830.com.common.R.drawable.ic_3dollars_logo),
                contentDescription = "로고",
                modifier = Modifier.padding(top = 20.dp, start = 47.dp, end = 47.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 47.dp, end = 47.dp, top = 40.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
        }
    }
}

@Composable
private fun Footer(
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .navigationBarsPadding()
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(id = zion830.com.common.R.drawable.ic_instagram),
            contentDescription = "instagrem"
        )
        Text(
            text = "가슴속 3천원 인스타그램 바로가기",
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            color = Color.White,
            fontSize = dpToSp(dp = 12)
        )
    }
}

@Composable
private fun TopBar(clickBack: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(56.dp),
    ) {
        IconButton(
            onClick = clickBack,
            modifier = Modifier
                .padding(start = 8.dp)
                .wrapContentSize()
                .align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(id = zion830.com.common.R.drawable.ic_back_white),
                contentDescription = "뒤로가기"
            )
        }
        Text(
            text = stringResource(id = CommonR.string.str_team_title), fontSize = dpToSp(dp = 16),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W400,
            color = Color.White, modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun MyPageTeamMember(
    role: String,
    members: ImmutableList<String>
) {
    Row(
        modifier = Modifier
            .background(Pink500, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = role,
                fontSize = dpToSp(dp = 12),
                color = Pink200,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

@Composable
private fun AdButton(
    clickAd: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { clickAd() }
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "커피 ☕️ 사주기",
                fontSize = dpToSp(dp = 12),
                color = Pink,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "광고 보기",
                    fontSize = dpToSp(dp = 16),
                    color = Color.Black,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.W700,
                )
                Spacer(
                    modifier = Modifier.width(2.dp)
                )
                Icon(
                    painter = painterResource(id = zion830.com.common.R.drawable.ic_white_arrow),
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = Gray40
                )
            }
        }
    }
}


@Preview
@Composable
private fun PreviewAdButton() {
    AdButton {  }
}

@Preview
@Composable
private fun PreviewMyPageTeamMember() {
    MyPageTeamMember(
        role = "Role",
        members = persistentListOf("Member1", "Member2")
    )
}