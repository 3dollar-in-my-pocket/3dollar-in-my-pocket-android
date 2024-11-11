package com.my.presentation.page.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.my.presentation.page.MyPageViewModel
import com.my.presentation.page.commponent.MyPageShopInfoView
import com.my.presentation.page.data.MyPageButton
import com.my.presentation.page.data.MyPageSectionTitleData
import com.my.presentation.page.data.MyPageShop
import com.my.presentation.page.data.MyPageUserInformationData
import com.my.presentation.page.data.MyVoteHistory
import com.my.presentation.page.data.myPageButtonPreview
import com.my.presentation.page.data.myPageSectionTitlePreview
import com.my.presentation.page.data.myPageShopPreview
import com.my.presentation.page.data.myPageShopsPreview
import com.my.presentation.page.data.myPageUserInformationDataPreview
import com.my.presentation.page.data.toMyPageButtons
import com.my.presentation.page.data.toMyPageShops
import com.my.presentation.page.data.toMyVoteHistory
import com.threedollar.common.listener.MyFragments
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.data.poll.response.GetMyPollListResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.data.visit_history.MyVisitHistoryResponseV2
import zion830.com.common.base.compose.ColorWhite
import zion830.com.common.base.compose.Gray10
import zion830.com.common.base.compose.Gray100
import zion830.com.common.base.compose.Gray30
import zion830.com.common.base.compose.Gray40
import zion830.com.common.base.compose.Gray50
import zion830.com.common.base.compose.Gray60
import zion830.com.common.base.compose.Gray70
import zion830.com.common.base.compose.Gray80
import zion830.com.common.base.compose.Gray90
import zion830.com.common.base.compose.Gray95
import zion830.com.common.base.compose.Pink
import zion830.com.common.base.compose.PretendardFontFamily
import zion830.com.common.base.compose.Red
import zion830.com.common.base.compose.dpToSp

@Composable
fun MyPageScreen(viewModel: MyPageViewModel) {

    val userInfo by viewModel.userInfo.collectAsState(UserWithDetailApiResponse())
    val myFavoriteStores by viewModel.myFavoriteStores.collectAsState(MyFavoriteFolderResponse())
    val myVisitsStore by viewModel.myVisitsStore.collectAsState(MyVisitHistoryResponseV2())
    val userPollList by viewModel.userPollList.collectAsState(GetMyPollListResponse())

    val myPageUserInformation by remember(userInfo) {
        derivedStateOf {
            MyPageUserInformationData(
                name = userInfo.name,
                medal = userInfo.representativeMedal
            )
        }
    }
    val myPageButtons = userInfo.toMyPageButtons({ viewModel.addFragments(MyFragments.MyStore) },
        { viewModel.addFragments(MyFragments.MyReview) },
        { viewModel.addFragments(MyFragments.MyMedal) })
    val myVisitsShop by remember(myVisitsStore) { mutableStateOf(myVisitsStore.toMyPageShops()) }
    val myFavoriteShop by remember(myFavoriteStores) { mutableStateOf(myFavoriteStores.toMyPageShops()) }
    val myVoteHistory by remember(userPollList) { mutableStateOf(userPollList.polls?.contents.orEmpty().map { it.poll.toMyVoteHistory() }) }


    Scaffold(modifier = Modifier
        .fillMaxSize(1f),
        backgroundColor = Gray100,
        topBar = { MyPageTitle { viewModel.addFragments(MyFragments.MyPageSetting) } })
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            MyPageUserInformation(myPageUserInformation)
            MyPageInformationButtons(myPageButtons)
            Spacer(modifier = Modifier.height(44.dp))
            // 내가 방문한 가게
            MyPageSectionTitle(MyPageSectionTitleData(
                topTitle = stringResource(R.string.str_section_title_visite),
                topIcon = zion830.com.common.R.drawable.ic_badge_gray,
                bottomTitle = stringResource(R.string.str_section_bottom_visite),
                count = userInfo.activities.visitStoreCount
            ) { viewModel.addFragments(MyFragments.MyVisitHistory) })
            Spacer(modifier = Modifier.height(16.dp))
            if (myVisitsShop.isEmpty()) {
                MyPageEmptyView(
                    zion830.com.common.R.drawable.img_empty,
                    stringResource(R.string.str_visit_empty_title),
                    stringResource(R.string.str_visit_empty_message)
                )
            } else {
                MyPageVisitedShopItem(myVisitsShop, true) { myPageShop -> viewModel.clickStore(myPageShop) }
            }
            Spacer(modifier = Modifier.height(36.dp))

            // 내가 좋아하는 가게
            MyPageSectionTitle(MyPageSectionTitleData(
                topTitle = stringResource(R.string.str_section_title_favorit),
                topIcon = zion830.com.common.R.drawable.ic_favorite_gray,
                bottomTitle = stringResource(R.string.str_section_bottom_favorit),
                count = userInfo.activities.favoriteStoreCount
            ) { viewModel.clickFavorite() })
            Spacer(modifier = Modifier.height(16.dp))
            if (myFavoriteShop.isEmpty()) {
                MyPageEmptyView(
                    zion830.com.common.R.drawable.img_empty,
                    stringResource(R.string.str_favorite_empty_title),
                    stringResource(R.string.str_favorite_empty_message)
                )
            } else {
                MyPageVisitedShopItem(myFavoriteShop, false) { myPageShop -> viewModel.clickStore(myPageShop) }
            }
            Spacer(modifier = Modifier.height(36.dp))

            // 맛대맛 투표
            MyPageSectionTitle(MyPageSectionTitleData(
                topTitle = stringResource(R.string.str_section_title_vote),
                topIcon = zion830.com.common.R.drawable.ic_fire,
                bottomTitle = stringResource(R.string.str_section_bottom_vote)
            ) {})
            Spacer(modifier = Modifier.height(16.dp))
            if (myVoteHistory.isEmpty()) {
                MyPageEmptyView(
                    zion830.com.common.R.drawable.ic_fire_disabled,
                    stringResource(R.string.str_vote_empty_title),
                    stringResource(R.string.str_vote_empty_message)
                )
                Spacer(modifier = Modifier.height(44.dp))
            } else {
                MyPageVoteCountItem(userPollList.meta?.totalParticipantsCount ?: 0)
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .background(color = Gray95, shape = RoundedCornerShape(16.dp))
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    myVoteHistory.forEach { vote ->
                        MyPageVoteHistoryItem(vote)
                    }
                }
            }
            Spacer(modifier = Modifier.height(44.dp))
            MyPageTeamMoveScreen {
                viewModel.clickTeam()
            }
        }
    }
}

@Preview
@Composable
fun MyPageTitle(clickSetting: () -> Unit = {}) {
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
            onClick = clickSetting, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterEnd)
        ) {
            Image(
                painter = painterResource(id = zion830.com.common.R.drawable.ic_setting),
                contentDescription = "마이페이지 셋팅 아이콘"
            )
        }
    }
}

@Preview
@Composable
fun MyPageUserInformation(myPageUserInformation: MyPageUserInformationData = myPageUserInformationDataPreview) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = zion830.com.common.R.drawable.img_back_gray),
            contentDescription = "내 정보 배경"
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            AsyncImage(
                modifier = Modifier
                    .size(90.dp),
                model = myPageUserInformation.medal.iconUrl,
                contentDescription = "내 칭호 사진",
                placeholder = painterResource(id = zion830.com.common.R.drawable.ic_no_store),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = myPageUserInformation.medal.name.orEmpty(),
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
                text = myPageUserInformation.name, fontSize = dpToSp(dp = 30),
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        buttonItems.forEachIndexed { index, myPageButton ->
            MyPageInformationButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                topText = myPageButton.topText,
                bottomText = myPageButton.bottomText,
                onClick = myPageButton.onClick
            )
            if (index < buttonItems.size - 1) {
                Divider(
                    color = Gray80,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun MyPageSectionTitle(myPageSectionTitle: MyPageSectionTitleData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = myPageSectionTitle.topIcon),
                contentDescription = myPageSectionTitle.topTitle,
                modifier = Modifier.size(16.dp),
                tint = Gray50
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable { myPageSectionTitle.onClick() },
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
            if (myPageSectionTitle.count != null && myPageSectionTitle.count > 0) {
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
                        painter = painterResource(id = zion830.com.common.R.drawable.ic_white_arrow),
                        tint = Color.White,
                        contentDescription = "화살표",
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MyPageInformationButton(
    modifier: Modifier = Modifier,
    topText: String = "상단",
    bottomText: String = "하단",
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
fun MyPageEmptyView(
    @DrawableRes icon: Int = zion830.com.common.R.drawable.img_empty,
    title: String = "가게 상세에서 추가해 보세요",
    message: String = "가게 상세에서 추가해 보세요"
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(Gray95, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(modifier = Modifier.size(32.dp), painter = painterResource(id = icon), contentDescription = "icon")
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title, fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W700,
            color = Gray60, fontSize = dpToSp(dp = 16)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message, fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            color = Gray70, fontSize = dpToSp(dp = 12)
        )
    }
}

@Preview
@Composable
fun MyVisitedShopDateItem(visitedData: MyPageShop.ShopVisitedData = myPageShopPreview.visitedData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .clip(shape = RoundedCornerShape(13.dp))
            .background(Gray90),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(14.dp),
            painter = painterResource(id = if (visitedData.isExists) zion830.com.common.R.drawable.ic_face_smile else zion830.com.common.R.drawable.ic_face_sad),
            contentDescription = "방문 상태"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = visitedData.date, fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            color = Color.White, fontSize = dpToSp(dp = 12)
        )
    }
}

@Preview
@Composable
fun MyPageShopItem(
    isMyVisited: Boolean = true,
    myPageShop: MyPageShop = myPageShopPreview,
    clickShop: (MyPageShop) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Gray95)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 16.dp)
            .clickable { clickShop(myPageShop) }
    ) {
        if (isMyVisited) {
            MyVisitedShopDateItem(visitedData = myPageShop.visitedData)
            Spacer(modifier = Modifier.height(16.dp))
        }
        MyPageShopInfoView(myPageShop = myPageShop)
    }
}

@Preview
@Composable
fun MyPageVisitedShopItem(
    myPageShops: List<MyPageShop> = myPageShopsPreview,
    isMyVisited: Boolean = true,
    clickShop: (MyPageShop) -> Unit = {}
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.width(8.dp))
        myPageShops.forEach {
            MyPageShopItem(isMyVisited = isMyVisited, myPageShop = it, clickShop = clickShop)
        }
        Spacer(Modifier.width(8.dp))
    }
}

@Preview
@Composable
fun MyPageVoteCountItem(count: Int = 2042) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Gray95)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = zion830.com.common.R.drawable.ic_fire),
            contentDescription = "투표"
        )
        Text(
            text = stringResource(id = R.string.str_mypage_count, count),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W700,
            color = Color.White,
            fontSize = dpToSp(dp = 24)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(id = R.string.str_mypage_vote_count_description),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            color = Color.White,
            fontSize = dpToSp(dp = 12)
        )
    }
}

@Composable
fun MyPageVoteHistoryItem(vote: MyVoteHistory) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = vote.title,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                color = Gray10,
                fontSize = dpToSp(dp = 16),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = vote.date,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W500,
                color = Gray40,
                fontSize = dpToSp(dp = 10)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            vote.options.forEach {
                MyPageVoteItem(modifier = Modifier.weight(1f), it)
            }
        }
    }
}

@Composable
fun MyPageVoteItem(modifier: Modifier = Modifier, option: MyVoteHistory.Option) {
    Column(
        modifier = modifier
            .height(90.dp)
            .border(
                width = 1.dp,
                color = if (option.isTopVote) Red else Gray70,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = option.name,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W500,
            color = if (option.isTopVote) ColorWhite else Gray60,
            fontSize = dpToSp(dp = 12),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(
                    id = R.string.str_vote_percent,
                    if (option.isTopVote) "\uD83E\uDD23" else "\uD83D\uDE1E",
                    option.ratio
                ),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                color = if (option.isTopVote) Color.White else Gray60,
                fontSize = dpToSp(dp = 16)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "${option.choiceCount}명",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W500,
                color = if (option.isTopVote) Color.White else Gray60,
                fontSize = dpToSp(dp = 10)
            )
        }
    }
}

@Preview
@Composable
fun MyPageTeamMoveScreen(clickTeam: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .clickable { clickTeam() }
            .fillMaxWidth()
            .background(color = Pink)
            .padding(20.dp)
    ) {
        Text(
            text = "\uD83D\uDE18 가슴속 3천원 팀원 소개",
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            color = Color.White,
            fontSize = dpToSp(dp = 14)
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(painter = painterResource(id = zion830.com.common.R.drawable.ic_white_arrow), contentDescription = "", modifier = Modifier.size(20.dp))
    }
}

@Preview
@Composable
fun MyPageFavoriteShopItem(
    myPageShops: List<MyPageShop> = myPageShopsPreview
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        myPageShops.forEach {
            MyPageShopItem(isMyVisited = false, myPageShop = it)
        }
    }
}

@Preview
@Composable
fun MyPageInformationButtonsView(buttonItems: List<MyPageButton> = myPageButtonPreview) {
    MyPageInformationButtons(buttonItems = buttonItems)
}

@Preview
@Composable
fun MyPageInformationTitlesView(titleItems: List<MyPageSectionTitleData> = myPageSectionTitlePreview) {
    Column {
        titleItems.forEach { myPageSectionTitle ->
            MyPageSectionTitle(myPageSectionTitle = myPageSectionTitle)
        }
    }
}