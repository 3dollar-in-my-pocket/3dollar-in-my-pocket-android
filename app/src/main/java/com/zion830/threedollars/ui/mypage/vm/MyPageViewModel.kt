package com.zion830.threedollars.ui.mypage.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.v2.request.UpdateMedalRequest
import com.zion830.threedollars.repository.model.v2.response.my.Medal
import com.zion830.threedollars.repository.model.v2.response.my.UserActivityData
import com.zion830.threedollars.repository.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.ui.mypage.adapter.MyMedal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class MyPageViewModel : BaseViewModel() {

    private val service = RetrofitBuilder.newServiceApi

    private val _userActivity: MutableLiveData<UserActivityData?> = MutableLiveData()
    val userActivity: LiveData<UserActivityData?> = _userActivity

    private val _myVisitHistory: MutableLiveData<List<VisitHistoryContent>?> = MutableLiveData()
    val myVisitHistory: LiveData<List<VisitHistoryContent>?> = _myVisitHistory

    private val _myMedals: MutableLiveData<List<MyMedal>> = MutableLiveData()
    val myMedals: MutableLiveData<List<MyMedal>> = _myMedals

    val selectedMedal = Transformations.map(userActivity) { it?.medal }

    private val _allMedals: MutableLiveData<List<Medal>> = MutableLiveData()
    val allMedals: LiveData<List<Medal>> = _allMedals

    fun initAllMedals() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = service.getMedals()
            if (response.isSuccessful) {
                _allMedals.postValue(response.body()?.data ?: emptyList())
            }
        }
    }

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

    fun requestMyMedals() {
        if (_allMedals.value.isNullOrEmpty()) {
            initAllMedals()
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = service.getMyMedals()
            _myMedals.postValue(_allMedals.value?.map { medal ->
                val hasMedal = (response.body()?.data ?: emptyList()).find { it.medalId == medal.medalId } != null
                val isSelected = medal.medalId == _userActivity.value?.medal?.medalId
                MyMedal(medal, isSelected, hasMedal)
            } ?: emptyList())
        }
    }

    fun changeMedal(medalId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = service.updateMyMedals(UpdateMedalRequest(medalId))
            if (response.isSuccessful) {
                requestUserActivity()
            } else {
                _msgTextId.postValue(R.string.error_change_medal)
                _msgTextId.postValue(-1)
            }
        }
    }
}
