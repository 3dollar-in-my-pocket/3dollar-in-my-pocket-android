package com.zion830.threedollars

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zion830.threedollars.repository.MyReviewDataSource
import com.zion830.threedollars.repository.MyStoreDataSource
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.response.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
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

    private val _myStore: MutableLiveData<MyStoreResponse> = MutableLiveData()
    val myStore: LiveData<MyStoreResponse>
        get() = _myStore

    private val _myReview: MutableLiveData<MyReviewResponse> = MutableLiveData()
    val myReview: LiveData<MyReviewResponse>
        get() = _myReview

    val myAllStore: LiveData<PagedList<Store>> by lazy {
        LivePagedListBuilder(
            MyStoreDataSource.Factory(viewModelScope + coroutineExceptionHandler), MyStoreDataSource.pageConfig
        ).build()
    }

    val myAllReview: LiveData<PagedList<Review>> by lazy {
        LivePagedListBuilder(
            MyReviewDataSource.Factory(viewModelScope + coroutineExceptionHandler), MyReviewDataSource.pageConfig
        ).build()
    }

    fun updateUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                _userInfo.postValue(userRepository.getUserInfo())
                _myStore.postValue(userRepository.getMyStore().await())
                _myReview.postValue(userRepository.getMyReviews().await())
            } catch (e: Exception) {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }

    fun updateName() {
        if (userName.value.isNullOrBlank()) {
            return
        }
        val handler = CoroutineExceptionHandler { _, t ->
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
        viewModelScope.launch(Dispatchers.IO + handler) {
            userRepository.updateName(userName.value!!).await()
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
    }
}