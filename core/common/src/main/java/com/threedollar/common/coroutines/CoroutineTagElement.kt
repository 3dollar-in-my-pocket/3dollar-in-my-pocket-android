package com.threedollar.common.coroutines

import kotlin.coroutines.CoroutineContext

data class CoroutineTagElement(val tag: Any) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<CoroutineTagElement>
    override val key: CoroutineContext.Key<*> = Key
}
