package com.zion830.threedollars.core.ui.component.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * [flow]를 생명주기에 맞춰 수집한다. 수집은 [flow]·생명주기 소유자가 바뀔 때만 다시 시작하지만,
 * [block]은 항상 최신 것을 호출한다. 그래야 재구성 사이에 바뀐 값(예: 새로 만든 `LazyListState`)에 효과가 적용된다.
 */
@Composable
fun <T> FlowWithLifecycleEffect(
    flow: Flow<T>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentBlock by rememberUpdatedState(block)

    LaunchedEffect(flow, lifecycleOwner) {
        flow.flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect { currentBlock(it) }
    }
}
