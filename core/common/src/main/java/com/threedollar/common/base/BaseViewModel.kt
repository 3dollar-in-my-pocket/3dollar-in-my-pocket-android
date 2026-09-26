package com.threedollar.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.threedollar.common.BuildConfig
import com.threedollar.common.R
import com.threedollar.common.analytics.ScreenName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response

open class BaseViewModel : ViewModel() {
    open val screenName: ScreenName = ScreenName.EMPTY

    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    protected val _msgTextId = MutableLiveData<Int>()
    val msgTextId: LiveData<Int> get() = _msgTextId

    protected val _serverError = MutableSharedFlow<String?>()
    val serverError: SharedFlow<String?> get() = _serverError

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        if (BuildConfig.DEBUG) t.printStackTrace()
        FirebaseCrashlytics.getInstance().log(t.message ?: t::class.java.simpleName)
    }

    fun showLoading() {
        _isLoading.postValue(true)
    }

    fun hideLoading() {
        _isLoading.postValue(false)
    }

    open fun handleError(t: Throwable) {
        _msgTextId.postValue(R.string.connection_failed)
        _msgTextId.postValue(-1)
    }

    protected suspend fun <T> safeApiCall(
        apiCall: Response<BaseResponse<T>>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): ResultWrapper<T?> = apiCall.toResultWrapper(dispatcher)
}