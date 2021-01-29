package com.zion830.threedollars

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zion830.threedollars.repository.MyReviewDataSource
import com.zion830.threedollars.repository.MyStoreDataSource
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.response.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.await
import zion830.com.common.base.BaseViewModel
import java.net.ConnectException

class UserInfoViewModel : BaseViewModel() {

    private val userRepository = UserRepository()

    private val _userInfo: MutableLiveData<UserInfoResponse> = MutableLiveData()

    val userInfo: LiveData<UserInfoResponse>
        get() = _userInfo

    private val _isAlreadyUsed: MutableLiveData<Boolean> = MutableLiveData()
    val isAlreadyUsed: LiveData<Boolean>
        get() = _isAlreadyUsed

    private val _isNameUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isNameUpdated: LiveData<Boolean>
        get() = _isNameUpdated

    val userName: MutableLiveData<String> = MutableLiveData("")

    val isNameEmpty: LiveData<Boolean> = Transformations.map(userName) {
        it.isNullOrBlank()
    }

    val myStore: LiveData<MyStoreResponse> = liveData(Dispatchers.IO + coroutineExceptionHandler) {
        emit(userRepository.getMyStore().await())
    }

    val myReview: LiveData<MyReviewResponse> = liveData(Dispatchers.IO + coroutineExceptionHandler) {
        emit(userRepository.getMyReviews().await())
    }

    val myAllStore: LiveData<PagedList<Store>> by lazy {
        LivePagedListBuilder(
            MyStoreDataSource.Factory(viewModelScope, Dispatchers.IO + coroutineExceptionHandler),
            MyStoreDataSource.pageConfig
        ).build()
    }

    val myAllReview: LiveData<PagedList<Review>> by lazy {
        LivePagedListBuilder(
            MyReviewDataSource.Factory(viewModelScope, Dispatchers.IO + coroutineExceptionHandler),
            MyReviewDataSource.pageConfig
        ).build()
    }

    fun updateUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _userInfo.postValue(userRepository.getUserInfo())
        }
    }

    fun updatePreviewData() {

    }

    fun updateName() {
        if (userName.value.isNullOrBlank()) {
            return
        }
        val updateNameHandler = CoroutineExceptionHandler { _, t ->
            when (t) {
                is HttpException -> {
                    _isAlreadyUsed.postValue(true)
                }
                is ConnectException -> {
                    _msgTextId.postValue(R.string.set_name_failed)
                }
                else -> {
                    _msgTextId.postValue(R.string.set_name_success)
                    _isNameUpdated.postValue(true)
                    updateUserInfo()
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO + updateNameHandler) {
            userRepository.updateName(userName.value!!).await()
        }
    }

    fun initNameUpdateInfo() {
        _isNameUpdated.value = false
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
    }
}