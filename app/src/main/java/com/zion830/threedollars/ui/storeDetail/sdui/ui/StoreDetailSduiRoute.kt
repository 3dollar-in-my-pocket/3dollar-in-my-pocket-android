package com.zion830.threedollars.ui.storeDetail.sdui.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.zion830.threedollars.ui.storeDetail.user.ui.compose.StoreDetailDisplayItemOverlay
import kotlinx.coroutines.launch

/**
 * [StoreDetailSduiViewModel] 과 [StoreDetailSduiContent] 를 잇는다. 효과 중 스크롤은 여기서, 나머지는 [navigator] 가 처리한다.
 */
@Composable
fun StoreDetailSduiRoute(
    viewModel: StoreDetailSduiViewModel,
    navigator: StoreDetailSduiNavigator,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderHeader: (@Composable () -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val tabHeightPx = with(LocalDensity.current) { SDStoreTabSectionDefaults.Height.roundToPx() }
    val slots = remember { storeDetailSduiSlots() }

    FlowWithLifecycleEffect(viewModel.effect) { effect ->
        if (effect is StoreDetailSduiUiEffect.ScrollToSection) {
            scope.launch { listState.scrollToSection(effect.index, viewModel.state.value.sections, tabHeightPx) }
        } else {
            navigator.handleEffect(effect)
        }
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
        )
        val displayItemState by viewModel.displayItemState.collectAsStateWithLifecycle()
        StoreDetailDisplayItemOverlay(
            state = displayItemState,
            onDisplayed = { viewModel.dispatch(StoreDetailSduiUiIntent.OnDisplayItemDisplayed(it)) },
            onVisitClick = { viewModel.dispatch(StoreDetailSduiUiIntent.OnVisitInducementClick(it)) },
            onReasonClick = { viewModel.dispatch(StoreDetailSduiUiIntent.OnDisappearanceReasonClick(it)) },
            onReportClick = { viewModel.dispatch(StoreDetailSduiUiIntent.OnDisappearanceReportClick) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = StoreDetailSduiDefaults.DisplayItemBottomInset),
        )
        if (state.isUploading) {
            CircularProgressIndicator(color = Pink, modifier = Modifier.align(Alignment.Center))
        }
    }
}
