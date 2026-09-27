package com.zion830.threedollars.ui.storeDetail.post.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import base.compose.AppTheme
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray40
import base.compose.Gray60
import base.compose.Gray95
import base.compose.PretendardFontFamily
import base.compose.Red
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.domain.home.data.store.ImageModel
import com.threedollar.domain.home.data.store.NewsPostModel
import com.zion830.threedollars.core.ui.component.compose.LottieFishLoading
import com.zion830.threedollars.core.ui.component.compose.components.FlowWithLifecycleEffect
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.ui.dialog.ReviewPhotoDialog
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListUiEffect
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListUiIntent
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListUiState
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostTime
import com.zion830.threedollars.ui.storeDetail.post.model.imageSections
import com.zion830.threedollars.ui.storeDetail.post.model.likeSticker
import com.zion830.threedollars.ui.storeDetail.post.viewModel.StorePostListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 사장님 가게 소식 목록 (딥링크 `postList?storeId=`). iOS `BossStorePostListViewController` 와 같은 화면.
 */
@AndroidEntryPoint
class StorePostListActivity : AppCompatActivity() {

    private val viewModel: StorePostListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
        )
        setContent {
            AppTheme {
                StorePostListRoute(
                    viewModel = viewModel,
                    onEffect = ::handleEffect,
                )
            }
        }
    }

    private fun handleEffect(effect: StorePostListUiEffect) {
        when (effect) {
            StorePostListUiEffect.Close -> finish()
            is StorePostListUiEffect.ShowImages -> ReviewPhotoDialog
                .getInstance(effect.imageUrls.map { ImageModel(imageUrl = it, width = 0, height = 0, ratio = 0) }, effect.startIndex)
                .show(supportFragmentManager, ReviewPhotoDialog::class.java.name)

            is StorePostListUiEffect.ShowErrorAlert -> showErrorAlert(effect.message)
        }
    }

    private fun showErrorAlert(message: String?) {
        if (isFinishing) return
        AlertDialog.Builder(this)
            .setMessage(message ?: getString(CommonR.string.store_detail_error_default))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    companion object {
        const val EXTRA_STORE_ID = "extra_store_id"

        fun getIntent(context: Context, storeId: String): Intent =
            Intent(context, StorePostListActivity::class.java).putExtra(EXTRA_STORE_ID, storeId)
    }
}

@Composable
private fun StorePostListRoute(
    viewModel: StorePostListViewModel,
    onEffect: (StorePostListUiEffect) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(StorePostListUiIntent.OnInit)
    }
    FlowWithLifecycleEffect(viewModel.effect) { onEffect(it) }

    StorePostListScreen(
        state = state,
        onIntent = viewModel::dispatch,
    )
}

@Composable
private fun StorePostListScreen(
    state: StorePostListUiState,
    onIntent: (StorePostListUiIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    PagingTrigger(
        listState = listState,
        canLoadMore = state.canLoadMore && state.posts.isNotEmpty(),
        onLoadNextPage = { onIntent(StorePostListUiIntent.OnLoadNextPage) },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorWhite)
            .statusBarsPadding(),
    ) {
        StorePostListTopBar(
            storeName = state.storeName,
            onBack = { onIntent(StorePostListUiIntent.OnBackClick) },
        )
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
            ) {
                itemsIndexed(state.posts, key = { _, post -> post.postId }) { index, post ->
                    StorePostCard(
                        post = post,
                        onImageClick = { imageIndex -> onIntent(StorePostListUiIntent.OnImageClick(post.postId, imageIndex)) },
                        onLikeClick = { onIntent(StorePostListUiIntent.OnLikeClick(post.postId)) },
                    )
                    if (index < state.posts.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            thickness = 1.dp,
                            color = Gray10,
                        )
                    }
                }
                if (state.isLoading && state.posts.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            LottieFishLoading(modifier = Modifier.size(72.dp))
                        }
                    }
                }
                item { Spacer(modifier = Modifier.navigationBarsPadding()) }
            }
            if (state.isLoading && state.posts.isEmpty()) {
                LottieFishLoading(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(120.dp),
                )
            }
        }
    }
}

@Composable
private fun StorePostListTopBar(
    storeName: String,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorWhite),
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(24.dp)
                .noRippleClickable(onClick = onBack),
        )
        if (storeName.isNotBlank()) {
            Text(
                text = stringResource(CommonR.string.store_post_list_title, storeName),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 56.dp),
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(16),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun StorePostCard(
    post: NewsPostModel,
    onImageClick: (Int) -> Unit,
    onLikeClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StorePostHeader(post = post, modifier = Modifier.padding(horizontal = 16.dp))

        val images = post.imageSections
        if (images.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(images) { index, section ->
                    AsyncImage(
                        model = section.url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(IMAGE_HEIGHT_DP.dp)
                            .width((IMAGE_HEIGHT_DP * section.ratio.takeIf { it > 0f }.orDefaultRatio()).dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Gray10)
                            .noRippleClickable { onImageClick(index) },
                    )
                }
            }
        }

        Text(
            text = post.body,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Gray95,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dpToSp(14),
            lineHeight = dpToSp(20),
        )

        StorePostLikeButton(
            post = post,
            onClick = onLikeClick,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun StorePostHeader(
    post: NewsPostModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = post.storeCategoryImageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = post.storeName,
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(14),
                lineHeight = dpToSp(20),
                maxLines = 1,
            )
            Text(
                text = storePostTimeText(post.updatedAt),
                color = Gray40,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = dpToSp(12),
                lineHeight = dpToSp(18),
            )
        }
    }
}

@Composable
private fun StorePostLikeButton(
    post: NewsPostModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val sticker = post.likeSticker
    Row(
        modifier = modifier.noRippleClickable {
            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(
                if (sticker.reactedByMe) DesignSystemR.drawable.ic_heart_fill else DesignSystemR.drawable.ic_heart_line,
            ),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = stringResource(CommonR.string.str_like, sticker.count),
            color = if (sticker.reactedByMe) Red else Gray60,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(10),
        )
    }
}

@Composable
private fun storePostTimeText(serverDateTime: String): String =
    when (val time = StorePostTime.of(serverDateTime)) {
        StorePostTime.JustNow -> stringResource(CommonR.string.store_post_just_now)
        is StorePostTime.Relative -> DateUtils.getRelativeTimeSpanString(
            time.epochMillis,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
        ).toString()

        is StorePostTime.Date -> time.text
        null -> ""
    }

@Composable
private fun PagingTrigger(
    listState: LazyListState,
    canLoadMore: Boolean,
    onLoadNextPage: () -> Unit,
) {
    LaunchedEffect(listState, canLoadMore) {
        snapshotFlow {
            val totalCount = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex to totalCount
        }
            .map { (lastVisibleIndex, totalCount) ->
                canLoadMore && totalCount > 0 && lastVisibleIndex >= totalCount - 2
            }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadNextPage() }
    }
}

private fun Float?.orDefaultRatio(): Float = this ?: 1f

private const val IMAGE_HEIGHT_DP = 208
