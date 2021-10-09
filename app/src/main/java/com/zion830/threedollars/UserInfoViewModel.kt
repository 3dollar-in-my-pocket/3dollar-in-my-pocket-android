package com.zion830.threedollars

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zion830.threedollars.repository.MyReviewDataSource
import com.zion830.threedollars.repository.MyStoreDataSource
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.response.*
import com.zion830.threedollars.repository.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.repository.model.v2.response.my.MyReviews
import com.zion830.threedollars.repository.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.utils.NaverMapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel

class UserInfoViewModel : BaseViewModel() {

    private val userRepository = UserRepository()

    private val _userInfo: MutableLiveData<MyInfoResponse> = MutableLiveData()

    val userInfo: LiveData<MyInfoResponse>
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

    private val refresh: MutableLiveData<Boolean> = MutableLiveData(true)

    val myStore: LiveData<List<StoreInfo>?> = refresh.switchMap {
        liveData(Dispatchers.IO + coroutineExceptionHandler) {
            emit(
                userRepository.getMyStore(NaverMapUtils.DEFAULT_LOCATION.latitude, NaverMapUtils.DEFAULT_LOCATION.longitude, 0).body()?.data?.contents
            )
        }
    }

    val myReview: LiveData<MyReviews?> = refresh.switchMap {
        liveData(Dispatchers.IO + coroutineExceptionHandler) {
            emit(userRepository.getMyReviews(0).body()?.data)
        }
    }

    val myAllStore: LiveData<PagedList<StoreInfo>> by lazy {
        LivePagedListBuilder(
            MyStoreDataSource.Factory(viewModelScope, Dispatchers.IO + coroutineExceptionHandler, NaverMapUtils.DEFAULT_LOCATION),
            MyStoreDataSource.pageConfig
        ).build()
    }

    val myAllReview: LiveData<PagedList<ReviewDetail>> by lazy {
        LivePagedListBuilder(
            MyReviewDataSource.Factory(viewModelScope, Dispatchers.IO + coroutineExceptionHandler),
            MyReviewDataSource.pageConfig
        ).build()
    }

    fun updateUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _userInfo.postValue(userRepository.getMyInfo().body())
            refresh.postValue(true)
        }
    }

    fun updateName() {
        if (userName.value.isNullOrBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val checkName = userRepository.checkName(userName.value!!)
            if (checkName.body()?.resultCode?.isNotBlank() == true) {
                _isAlreadyUsed.postValue(true)
                return@launch
            }

            val result = userRepository.updateName(userName.value!!)
            if (result.isSuccessful) {
                _msgTextId.postValue(R.string.set_name_success)
                _isNameUpdated.postValue(true)
                updateUserInfo()
            } else {
                _isAlreadyUsed.postValue(true)
            }
        }
    }

    fun initNameUpdateInfo() {
        _isNameUpdated.value = false
    }

    fun clearName() {
        userName.value = ""
    }

    fun deleteUser(onSuccess: () -> Unit) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = userRepository.signout()

            withContext(Dispatchers.Main) {
                hideLoading()

                if (result.isSuccessful) {
                    onSuccess()
                } else {
                    _msgTextId.postValue(R.string.failed_delete_account)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
    }
}