package com.threedollar.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.threedollar.common.BuildConfig
import com.threedollar.common.coroutines.CoroutineTagElement
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class UdfViewModel<Intent, State, Effect> : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    abstract val state: StateFlow<State>
    abstract val effect: Flow<Effect>

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, t ->
        if (BuildConfig.DEBUG) t.printStackTrace()

        onException(
            exception = t,
            tag = context[CoroutineTagElement.Key],
        )
    }

    private val dispatcher = Dispatchers.Main.immediate + coroutineExceptionHandler

    protected open fun onException(exception: Throwable, tag: Any? = null) {
        FirebaseCrashlytics.getInstance().log(exception.message ?: exception.toString())
    }

    abstract fun dispatch(intent: Intent)

    protected fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        tag: Any? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job = viewModelScope.launch(
        context = if (tag != null) {
            context + dispatcher + CoroutineTagElement(tag)
        } else {
            context + dispatcher
        },
        start = start,
        block = block
    )

    protected fun setLoading(enable: Boolean) = _loading.update { enable }
}
