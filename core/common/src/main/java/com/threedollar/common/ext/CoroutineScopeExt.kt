package com.threedollar.common.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend inline fun <T> CoroutineScope.runWithDelayedAction(
    delayMillis: Long = 100L,
    crossinline delayed: () -> Unit,
    crossinline block: suspend CoroutineScope.() -> T
): T {
    val job = launch {
        delay(delayMillis)
        delayed()
    }

    return try {
        block()
    } finally {
        job.cancel()
    }
}
