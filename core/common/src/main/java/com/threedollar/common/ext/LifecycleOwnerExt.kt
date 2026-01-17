package com.threedollar.common.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun LifecycleOwner.repeatOn(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit,
): Job = lifecycleScope.launch {
    repeatOnLifecycle(
        state = state,
        block = block
    )
}

fun <T> LifecycleOwner.repeatCollectLatest(
    flow: Flow<T>,
    state: Lifecycle.State,
    block: suspend (T) -> Unit,
): Job = repeatOn(state) {
    flow.collectLatest(block)
}

fun <T> LifecycleOwner.repeatCollect(
    flow: Flow<T>,
    state: Lifecycle.State,
    block: suspend (T) -> Unit,
): Job = repeatOn(state) {
    flow.collect(block)
}
