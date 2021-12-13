package com.zion830.threedollars.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.v2.response.my.UserActivityData
import com.zion830.threedollars.repository.model.v2.response.visit_history.VisitHistoryContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class MyPageViewModel : BaseViewModel() {

    private val service = RetrofitBuilder.newServiceApi

    private val _userActivity: MutableLiveData<UserActivityData?> = MutableLiveData()
    val userActivity: LiveData<UserActivityData?> = _userActivity

    private val _myVisitHistory: MutableLiveData<List<VisitHistoryContent>?> = MutableLiveData()
    val myVisitHistory: LiveData<List<VisitHistoryContent>?> = _myVisitHistory

    fun requestUserActivity() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = service.getUserActivity()
            if (response.isSuccessful) {
                val userActivity = response.body()?.data
                _userActivity.postValue(userActivity)
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }

    fun requestVisitHistory() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = service.getMyVisitHistory(null, 20)
            if (response.isSuccessful) {
                val myVisitHistory = response.body()?.data
                _myVisitHistory.postValue(myVisitHistory?.contents)
            }
        }
    }
}
