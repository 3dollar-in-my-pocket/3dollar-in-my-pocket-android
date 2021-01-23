package zion830.com.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
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

    fun showLoading() {
        _isLoading.value = true
    }

    fun hideLoading() {
        _isLoading.value = false
    }

    open fun handleError(t: Throwable) {
        t.printStackTrace()
        _msgTextId.postValue(R.string.connection_failed)
    }
}