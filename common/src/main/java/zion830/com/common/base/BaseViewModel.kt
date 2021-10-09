package zion830.com.common.base

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import zion830.com.common.R

open class BaseViewModel : ViewModel() {

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        t.printStackTrace()
        handleError(t)
    }

    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    protected val _msgTextId = MutableLiveData<Int>()
    val msgTextId: LiveData<Int> get() = _msgTextId

    protected val _serverError = MutableLiveData<Boolean>()
    val serverError: LiveData<Boolean> get() = _serverError

    fun showLoading() {
        _isLoading.postValue(true)
    }

    fun hideLoading() {
        _isLoading.postValue(false)
    }

    open fun handleError(t: Throwable) {
        _msgTextId.postValue(R.string.connection_failed)
    }

    protected suspend fun <T> safeApiCall(
        apiCall: Response<BaseResponse<T>>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): ResultWrapper<T?> {
        return withContext(dispatcher) {
            try {
                val result = apiCall

                if (result.code() == 503) {
                    _serverError.postValue(true)
                }

                if (result.isSuccessful) {
                    when {
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
                } else {
                    ResultWrapper.GenericError(result.code(), result.message())
                }
            } catch (throwable: Throwable) {
                ResultWrapper.NetworkError
            }
        }
    }
}