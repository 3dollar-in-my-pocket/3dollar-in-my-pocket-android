package com.zion830.threedollars.ui.my.page.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.Gray40
import base.compose.Pink
import base.compose.Pink200
import base.compose.Pink500
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.zion830.threedollars.ui.my.page.data.TeamRow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import zion830.com.common.R
import com.threedollar.common.R as CommonR

@Composable
fun MyPageTeamScreen(
    clickBack: () -> Unit,
    clickAd: () -> Unit = {},
    clickTeam: () -> Unit = {}
) {
    val teams = remember {
        persistentListOf(
            TeamRow(role = "iOS", members = persistentListOf("유현식", "김하경")),
            TeamRow(role = "Backend", members = persistentListOf("강승호", "고예림")),
            TeamRow(role = "Android", members = persistentListOf("정진용", "전두영")),
            TeamRow(role = "기획자", members = persistentListOf("이한나")),
            TeamRow(role = "Marketer", members = persistentListOf("공서연")),
            TeamRow(role = "Design", members = persistentListOf("이윤이", "박은지", "양민설", "조혜원")),
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
            Logo()
            TeamGrid(
                teams = teams,
                onClickAd = clickAd
            )
        }
    }
}

@Composable
private fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.ic_3dollars_logo),
        contentDescription = "로고",
        modifier = Modifier.padding(top = 20.dp, start = 47.dp, end = 47.dp)
    )
}

@Composable
private fun ColumnScope.TeamGrid(
    teams: ImmutableList<TeamRow>,
    onClickAd: () -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 47.dp, end = 47.dp, top = 40.dp
        ),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        items(
            items = teams,
            key = { it.role }
        ) {
            TeamRow(it)
        }
        item(key = "AdButton") {
            AdButton(onClickAd)
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
            .padding(bottom = 12.dp)
            .navigationBarsPadding()
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_instagram),
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
private fun TopBar(
    clickBack: () -> Unit
) {
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
                painter = painterResource(id = R.drawable.ic_back_white),
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
private fun TeamRow(
    teamRow: TeamRow
) {
    Row(
        modifier = Modifier.background(Pink500, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = teamRow.role,
                fontSize = dpToSp(dp = 12),
                color = Pink200,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            MemberNameGrid(
                names = teamRow.members
            )
        }
    }
}

@Composable
private fun MemberNameGrid(
    names: ImmutableList<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        names.chunked(2).forEach { chunked ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chunked.forEach {
                    Text(
                        text = it,
                        fontSize = dpToSp(dp = 16),
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = dpToSp(6),
                            maxFontSize = dpToSp(16)
                        ),
                        maxLines = 1,
                        color = Color.White,
                        fontFamily = PretendardFontFamily,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
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
                fontSize = dpToSp(dp = 10),
                autoSize = TextAutoSize.StepBased(
                    minFontSize = dpToSp(6),
                    maxFontSize = dpToSp(10)
                ),
                color = Pink,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
                maxLines = 1
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
                    painter = painterResource(id = R.drawable.ic_white_arrow),
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
private fun PreviewMyPageTeamScreen() {
    MyPageTeamScreen(
        clickBack = {},
        clickAd = {},
        clickTeam = {}
    )
}

@Preview(widthDp = 320)
@Composable
private fun PreviewMyPageTeamSmallWidthScreen() {
    MyPageTeamScreen(
        clickBack = {},
        clickAd = {},
        clickTeam = {}
    )
}
