package com.zion830.threedollars.ui.storeDetail.sdui.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import base.compose.Pink
import com.zion830.threedollars.core.ui.component.compose.components.FlowWithLifecycleEffect
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreTabSectionDefaults
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailDestination
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiEffect
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiIntent
import com.zion830.threedollars.ui.storeDetail.sdui.viewModel.StoreDetailSduiViewModel
import kotlinx.coroutines.launch

/**
 * [StoreDetailSduiViewModel] 과 [StoreDetailSduiContent] 를 잇는다. 효과 중 스크롤은 여기서, 나머지는 [navigator] 가 처리한다.
 *
 * @param listState 홈 시트처럼 네비를 [StoreDetailSduiNavigationBarRoute] 로 따로 그리면 같은 상태를 넘겨야 가게명 fade 가 맞는다.
 * @param inSheet [StoreDetailSduiContent] 참고.
 * @param collectEffects 홈 시트는 미리보기(tip)에서도 액션을 처리해야 해서 [StoreDetailSduiEffects] 를 시트 밖에서 항상 수집한다.
 * 이때 여기서 또 수집하면 효과를 나눠 받으므로 false 로 둔다.
 */
@Composable
fun StoreDetailSduiRoute(
    viewModel: StoreDetailSduiViewModel,
    navigator: StoreDetailSduiNavigator,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    inSheet: Boolean = false,
    collectEffects: Boolean = true,
    isDisplayed: Boolean = true,
    placeholderHeader: (@Composable () -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val slots = remember { storeDetailSduiSlots() }

    if (collectEffects) {
        StoreDetailSduiEffects(viewModel = viewModel, navigator = navigator, listState = listState)
    }

    Box(modifier = modifier.fillMaxSize()) {
        StoreDetailSduiContent(
            state = state,
            listState = listState,
            onBack = onBack,
            onClose = onClose,
            onFavoriteClick = { viewModel.dispatch(StoreDetailSduiUiIntent.OnFavoriteClick) },
            onAction = { viewModel.dispatch(StoreDetailSduiUiIntent.OnAction(it)) },
            onImageClick = { images, index ->
                val urls = images.mapNotNull { it.url }
                if (urls.isNotEmpty()) {
                    navigator.navigate(StoreDetailDestination.ShowImages(urls, index.coerceIn(0, urls.lastIndex)))
                }
            },
            onImpression = { key, log -> viewModel.dispatch(StoreDetailSduiUiIntent.OnImpression(key, log)) },
            slots = slots,
            placeholderHeader = placeholderHeader,
            inSheet = inSheet,
            isDisplayed = isDisplayed,
        )
        if (state.isUploading) {
            CircularProgressIndicator(color = Pink, modifier = Modifier.align(Alignment.Center))
        }
    }
}

/**
 * 홈 시트용 상단 네비. 시트 밖 고정 위치에 그려 시트가 올라오는 동안 fade 로 나타나게 한다.
 */
@Composable
fun StoreDetailSduiNavigationBarRoute(
    viewModel: StoreDetailSduiViewModel,
    listState: LazyListState,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StoreDetailNavigationBar(
        state = state,
        listState = listState,
        onBack = onBack,
        onFavoriteClick = { viewModel.dispatch(StoreDetailSduiUiIntent.OnFavoriteClick) },
        onClose = onClose,
        modifier = modifier,
    )
}

/**
 * 상세 ViewModel 의 효과(이동·다이얼로그·토스트·섹션 스크롤)를 처리한다. 효과는 한 곳에서만 수집해야 한다.
 */
@Composable
fun StoreDetailSduiEffects(
    viewModel: StoreDetailSduiViewModel,
    navigator: StoreDetailSduiNavigator,
    listState: LazyListState,
) {
    val scope = rememberCoroutineScope()
    val tabHeightPx = with(LocalDensity.current) { SDStoreTabSectionDefaults.Height.roundToPx() }
    FlowWithLifecycleEffect(viewModel.effect) { effect ->
        if (effect is StoreDetailSduiUiEffect.ScrollToSection) {
            scope.launch { listState.scrollToSection(effect.index, viewModel.state.value.sections, tabHeightPx) }
        } else {
            navigator.handleEffect(effect)
        }
    }
}
