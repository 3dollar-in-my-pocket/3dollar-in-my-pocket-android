package com.threedollar.common.base

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.threedollar.common.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import retrofit2.Response

open class BaseViewModel : ViewModel() {

    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    protected val _msgTextId = MutableLiveData<Int>()
    val msgTextId: LiveData<Int> get() = _msgTextId

    protected val _serverError = MutableSharedFlow<String?>()
    val serverError: SharedFlow<String?> get() = _serverError

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
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
    ): ResultWrapper<T?> {
        return withContext(dispatcher) {
            try {
                if (apiCall.isSuccessful) {
                    handleSuccessEvent(apiCall)
                } else {
                    ResultWrapper.GenericError(apiCall.code(), apiCall.message())
                }
            } catch (throwable: Throwable) {
                ResultWrapper.NetworkError
            }
        }
    }

    private fun <T> handleSuccessEvent(result: Response<BaseResponse<T>>) = when {
        result.body()?.resultCode.isNullOrEmpty() -> {
            ResultWrapper.Success(result.body()?.data)
        }

        result.body()?.resultCode?.isDigitsOnly() == true -> {
            ResultWrapper.GenericError(result.body()?.resultCode?.toInt(), result.body()?.message)
        }

        else -> {
            ResultWrapper.GenericError(null, result.body()?.message)
        }
    }
}