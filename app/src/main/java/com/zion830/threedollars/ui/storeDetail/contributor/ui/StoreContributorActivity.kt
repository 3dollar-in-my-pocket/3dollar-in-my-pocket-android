package com.zion830.threedollars.ui.storeDetail.contributor.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import base.compose.AppTheme
import base.compose.ColorWhite
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.serverdriven.model.SDActionBarModel
import com.threedollar.common.serverdriven.model.SDCardModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.core.ui.component.compose.LottieFishLoading
import com.zion830.threedollars.core.ui.component.compose.components.FlowWithLifecycleEffect
import com.zion830.threedollars.core.ui.serverdriven.SDActionButton
import com.zion830.threedollars.core.ui.serverdriven.SDCardRenderer
import com.zion830.threedollars.core.ui.serverdriven.SDSectionRenderer
import com.zion830.threedollars.ui.storeDetail.contributor.model.StoreContributorUiEffect
import com.zion830.threedollars.ui.storeDetail.contributor.model.StoreContributorUiIntent
import com.zion830.threedollars.ui.storeDetail.contributor.model.StoreContributorUiState
import com.zion830.threedollars.ui.storeDetail.contributor.viewModel.StoreContributorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Gray100 = androidx.compose.ui.graphics.Color(0xFF0F0F0F)
private val Gray30 = androidx.compose.ui.graphics.Color(0xFFE4E4E4)
private val Gray0 = androidx.compose.ui.graphics.Color(0xFFF7F7F7)

@AndroidEntryPoint
class StoreContributorActivity : ComponentActivity() {

    @Inject
    lateinit var actionHandler: StoreContributorActionHandler

    private val viewModel: StoreContributorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionHandler.attach(this)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
        )

        setContent {
            AppTheme {
                StoreContributorRoute(
                    viewModel = viewModel,
                    onClose = ::finish,
                    onAction = actionHandler::onAction,
                )
            }
        }
    }

    override fun onDestroy() {
        actionHandler.detach()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_STORE_ID = "extra_store_id"

        fun getIntent(
            context: Context,
            storeId: String,
        ): Intent = Intent(context, StoreContributorActivity::class.java).apply {
            putExtra(EXTRA_STORE_ID, storeId)
        }
    }
}

@Composable
private fun StoreContributorRoute(
    viewModel: StoreContributorViewModel,
    onClose: () -> Unit,
    onAction: (SDLinkModel) -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.dispatch(StoreContributorUiIntent.OnInit)
    }

    FlowWithLifecycleEffect(viewModel.effect) { effect ->
        when (effect) {
            StoreContributorUiEffect.Close -> onClose()
            is StoreContributorUiEffect.ExecuteAction -> onAction(effect.action)
        }
    }

    StoreContributorScreen(
        state = state,
        onClose = { viewModel.dispatch(StoreContributorUiIntent.OnCloseClick) },
        onActionClick = { viewModel.dispatch(StoreContributorUiIntent.OnActionClick(it)) },
        onLoadNextPage = { viewModel.dispatch(StoreContributorUiIntent.OnLoadNextPage) },
    )
}

@Composable
private fun StoreContributorScreen(
    state: StoreContributorUiState,
    onClose: () -> Unit,
    onActionClick: (SDLinkModel) -> Unit,
    onLoadNextPage: () -> Unit,
) {
    val listState = rememberLazyListState()
    if (state is StoreContributorUiState.Success) {
        PagingTrigger(
            listState = listState,
            canLoadMore = state.canLoadMore,
            isPaging = state.isPaging,
            onLoadNextPage = onLoadNextPage,
        )
    }

    val actionBar = (state as? StoreContributorUiState.Success)
        ?.screen
        ?.sections
        ?.filterIsInstance<SDSectionModel.ActionBarSection>()
        ?.firstOrNull()
        ?.actionBar
    val screenHeaderTitle = (state as? StoreContributorUiState.Success)
        ?.screen
        ?.sections
        ?.filterIsInstance<SDSectionModel.HeaderSection>()
        ?.firstOrNull { it.type.equals("SCREEN_HEADER", ignoreCase = true) }
        ?.header
        ?.title

    Scaffold(
        containerColor = Gray0,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            StoreContributorTopBar(
                title = screenHeaderTitle,
                onClose = onClose,
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorWhite)
                    .navigationBarsPadding(),
            ) {
                HorizontalDivider(color = Gray30)
                actionBar?.let {
                    BottomActionBar(
                        actionBar = it,
                        onActionClick = onActionClick,
                    )
                }
            }
        },
    ) { paddingValues ->
        when (state) {
            StoreContributorUiState.Loading -> LoadingContent(paddingValues)
            is StoreContributorUiState.Error -> ErrorContent(state.message, paddingValues)
            is StoreContributorUiState.Success -> SuccessContent(
                screen = state.screen,
                listState = listState,
                isPaging = state.isPaging,
            paddingValues = paddingValues,
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    actionBar: SDActionBarModel,
    onActionClick: (SDLinkModel) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SDActionButton(
            button = actionBar.button,
            fillMaxWidth = true,
            onAction = onActionClick,
        )
    }
}

@Composable
private fun PagingTrigger(
    listState: LazyListState,
    canLoadMore: Boolean,
    isPaging: Boolean,
    onLoadNextPage: () -> Unit,
) {
    LaunchedEffect(listState, canLoadMore, isPaging) {
        snapshotFlow {
            val totalCount = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex to totalCount
        }
            .map { (lastVisibleIndex, totalCount) ->
                canLoadMore && !isPaging && totalCount > 0 && lastVisibleIndex >= totalCount - 2
            }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                onLoadNextPage()
            }
    }
}

@Composable
private fun LoadingContent(
    paddingValues: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center,
    ) {
        LottieFishLoading(modifier = Modifier.size(120.dp))
    }
}

@Composable
private fun ErrorContent(
    message: String,
    paddingValues: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (message.isBlank()) "정보를 불러오지 못했어요" else message,
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(16),
            lineHeight = dpToSp(24),
        )
    }
}

@Composable
private fun SuccessContent(
    screen: SDScreenModel,
    listState: LazyListState,
    isPaging: Boolean,
    paddingValues: PaddingValues,
) {
    val bodySections = screen.sections.filterNot { section ->
        section is SDSectionModel.ActionBarSection ||
            (section is SDSectionModel.HeaderSection && section.type.equals("SCREEN_HEADER", ignoreCase = true))
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        state = listState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        bodySections.forEachIndexed { index, section ->
            when (section) {
                is SDSectionModel.HeaderSection -> {
                    item(key = "header-$index-${section.type}") {
                        SDSectionRenderer(section = section, onAction = {})
                    }
                }

                is SDSectionModel.CardsSection -> {
                    items(
                        items = section.cards,
                        key = { card -> "card-${section.type}-${card.cardId}" },
                    ) { card ->
                        SDCardRenderer(card = card)
                    }
                }

                is SDSectionModel.Unknown -> Unit
                is SDSectionModel.ActionBarSection -> Unit
            }
        }

        if (isPaging) {
            item(key = "paging") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LottieFishLoading(modifier = Modifier.size(72.dp))
                }
            }
        }
    }
}

@Composable
private fun StoreContributorTopBar(
    title: SDTextModel?,
    onClose: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .statusBarsPadding()
            .height(56.dp),
    ) {
        title?.let {
            Text(
                text = it.text,
                modifier = Modifier.align(Alignment.Center),
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = dpToSp(16),
                lineHeight = dpToSp(24),
            )
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_close_black),
                contentDescription = "닫기",
                tint = Gray100,
            )
        }
    }
}

@Preview
@Composable
private fun StoreContributorSuccessPreview() {
    AppTheme {
        StoreContributorScreen(
            state = StoreContributorUiState.Success(
                screen = contributorPreviewScreen(),
                canLoadMore = true,
            ),
            onClose = {},
            onActionClick = {},
            onLoadNextPage = {},
        )
    }
}

@Preview
@Composable
private fun StoreContributorErrorPreview() {
    AppTheme {
        StoreContributorScreen(
            state = StoreContributorUiState.Error("정보를 불러오지 못했어요"),
            onClose = {},
            onActionClick = {},
            onLoadNextPage = {},
        )
    }
}

private fun contributorPreviewScreen() = SDScreenModel(
    sections = listOf(
        SDSectionModel.HeaderSection(
            type = "SCREEN_HEADER",
            header = com.threedollar.common.serverdriven.model.SDHeaderModel(
                title = SDTextModel(
                    text = "정보 기여자 목록",
                    isHtml = false,
                    fontColor = "#141414",
                ),
            ),
        ),
        SDSectionModel.HeaderSection(
            type = "HEADER",
            header = com.threedollar.common.serverdriven.model.SDHeaderModel(
                title = SDTextModel(
                    text = "함께 만든 가게 정보",
                    isHtml = false,
                    fontColor = "#141414",
                ),
            ),
        ),
        SDSectionModel.CardsSection(
            type = "HISTORIES",
            cards = listOf(
                SDCardModel.HistoryCard(
                    type = "HISTORY_CARD",
                    cardId = "1",
                    title = SDTextModel("맛돌이", false, "#141414"),
                    subTitles = listOf(
                        SDTextModel("사진 12장 등록", false, "#666666"),
                        SDTextModel("메뉴 수정", false, "#666666"),
                    ),
                    metadata = SDTextModel("3시간 전", false, "#666666"),
                ),
                SDCardModel.HistoryCard(
                    type = "HISTORY_CARD",
                    cardId = "2",
                    title = SDTextModel("붕어빵러버", false, "#141414"),
                    subTitles = listOf(
                        SDTextModel("운영 시간 제보", false, "#666666"),
                    ),
                    metadata = SDTextModel("어제", false, "#666666"),
                ),
            ),
        ),
        SDSectionModel.ActionBarSection(
            type = "ACTION_BAR",
            actionBar = SDActionBarModel(
                button = com.threedollar.common.serverdriven.model.SDButtonModel(
                    text = SDTextModel("정보 공유하기", false, "#141414"),
                    link = SDLinkModel(type = "NONE", link = ""),
                ),
            ),
        ),
    ),
)
