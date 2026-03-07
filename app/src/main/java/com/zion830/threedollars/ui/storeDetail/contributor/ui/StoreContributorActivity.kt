package com.zion830.threedollars.ui.storeDetail.contributor.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.AppTheme
import base.compose.ColorWhite
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

class StoreContributorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
        )

        setContent {
            AppTheme {
                StoreContributorScreen(
                    contributors = contributorPreviewModels(),
                    onClose = ::finish,
                    onShareInfo = {},
                )
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, StoreContributorActivity::class.java)
    }
}

private data class ContributorUiModel(
    val name: String,
    val actions: List<String>,
    val relativeTime: String,
    val badgeStyle: ContributorBadgeStyle,
)

private enum class ContributorBadgeStyle {
    Mint,
    Coral,
}

@Composable
private fun StoreContributorScreen(
    contributors: List<ContributorUiModel>,
    onClose: () -> Unit,
    onShareInfo: () -> Unit,
) {
    Scaffold(
        containerColor = Gray0,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            StoreContributorTopBar(onClose = onClose)
        },
        bottomBar = {
            StoreContributorBottomBar(onShareInfo = onShareInfo)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = stringResource(CommonR.string.store_contributor_section_title),
                    color = Gray100,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = dpToSp(24),
                    lineHeight = dpToSp(32),
                )
            }

            items(contributors) { contributor ->
                ContributorCard(contributor = contributor)
            }
        }
    }
}

@Composable
private fun StoreContributorTopBar(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .statusBarsPadding()
            .height(56.dp),
    ) {
        Text(
            text = stringResource(CommonR.string.store_contributor_title),
            modifier = Modifier.align(Alignment.Center),
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dpToSp(16),
            lineHeight = dpToSp(24),
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_close_black),
                contentDescription = stringResource(CommonR.string.close),
                tint = Gray100,
            )
        }
    }
}

@Composable
private fun StoreContributorBottomBar(onShareInfo: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(color = Gray30)
        Button(
            onClick = onShareInfo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Gray30),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorWhite,
                contentColor = Gray100,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
            ),
        ) {
            Text(
                text = stringResource(CommonR.string.store_contributor_share_action),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = dpToSp(16),
                lineHeight = dpToSp(24),
            )
        }
    }
}

@Composable
private fun ContributorCard(contributor: ContributorUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ColorWhite)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        ContributorBadge(style = contributor.badgeStyle)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = contributor.name,
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(16),
                lineHeight = dpToSp(24),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    contributor.actions.forEach { action ->
                        Text(
                            text = action,
                            color = Gray50,
                            fontFamily = PretendardFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = dpToSp(12),
                            lineHeight = dpToSp(18),
                        )
                    }
                }

                Text(
                    text = contributor.relativeTime,
                    color = Gray50,
                    fontFamily = PretendardFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = dpToSp(12),
                    lineHeight = dpToSp(18),
                )
            }
        }
    }
}

@Composable
private fun ContributorBadge(style: ContributorBadgeStyle) {
    val outerColor = if (style == ContributorBadgeStyle.Mint) ComposeColor(0xFF6EDD56) else ComposeColor(0xFFFF9CA5)
    val accentColor = if (style == ContributorBadgeStyle.Mint) ComposeColor(0xFFFF7A45) else ComposeColor(0xFFFFD54A)
    val iconRes = if (style == ContributorBadgeStyle.Mint) {
        DesignSystemR.drawable.ic_location_soild_12
    } else {
        DesignSystemR.drawable.ic_write_16
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .background(outerColor, CircleShape)
            .border(2.dp, accentColor, CircleShape)
            .padding(5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(ComposeColor(0xFF232323)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = ColorWhite,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

private fun contributorPreviewModels() = listOf(
    ContributorUiModel(
        name = "아득한양갱#9",
        actions = listOf("사진 정보를 추가했어요.", "가게 위치를 수정했어요.", "가게 메뉴를 수정했어요."),
        relativeTime = "30분 전",
        badgeStyle = ContributorBadgeStyle.Mint,
    ),
    ContributorUiModel(
        name = "연희동 붕어빵",
        actions = listOf("사진 정보를 추가했어요."),
        relativeTime = "2일 전",
        badgeStyle = ContributorBadgeStyle.Mint,
    ),
    ContributorUiModel(
        name = "마포구 몽키스패너",
        actions = listOf("장소를 등록했어요", "장소를 등록했어요"),
        relativeTime = "15일 전",
        badgeStyle = ContributorBadgeStyle.Coral,
    ),
    ContributorUiModel(
        name = "마포구 몽키스패너",
        actions = listOf("가게를 등록했어요"),
        relativeTime = "15일 전",
        badgeStyle = ContributorBadgeStyle.Coral,
    ),
)

private val Gray0 = ComposeColor(0xFFFAFAFA)
private val Gray30 = ComposeColor(0xFFD0D0D0)
private val Gray50 = ComposeColor(0xFF969696)
private val Gray100 = ComposeColor(0xFF0F0F0F)

@Preview(showBackground = true, showSystemUi = true, widthDp = 375, heightDp = 812)
@Composable
private fun StoreContributorScreenPreview() {
    AppTheme {
        StoreContributorScreen(
            contributors = contributorPreviewModels(),
            onClose = {},
            onShareInfo = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun StoreContributorLongContentPreview() {
    AppTheme {
        StoreContributorScreen(
            contributors = listOf(
                ContributorUiModel(
                    name = "아주아주긴닉네임을가진기여자#999",
                    actions = listOf("사진 정보를 아주 길게 추가했어요.", "가게 위치를 아주 자세하게 수정했어요."),
                    relativeTime = "방금 전",
                    badgeStyle = ContributorBadgeStyle.Mint,
                ),
            ),
            onClose = {},
            onShareInfo = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun StoreContributorSingleActionPreview() {
    AppTheme {
        StoreContributorScreen(
            contributors = listOf(
                ContributorUiModel(
                    name = "연희동 붕어빵",
                    actions = listOf("가게를 등록했어요"),
                    relativeTime = "2일 전",
                    badgeStyle = ContributorBadgeStyle.Coral,
                ),
            ),
            onClose = {},
            onShareInfo = {},
        )
    }
}
