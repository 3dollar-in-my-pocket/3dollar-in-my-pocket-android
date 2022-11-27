package com.zion830.threedollars.ui.mypage.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.request.UpdateMedalRequest
import com.zion830.threedollars.datasource.model.v2.response.my.Medal
import com.zion830.threedollars.datasource.model.v2.response.my.UserActivityData
import com.zion830.threedollars.datasource.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.ui.mypage.adapter.MyMedal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val userDataSource: UserDataSource) :
    BaseViewModel() {

    private val _userActivity: SingleLiveEvent<UserActivityData?> = SingleLiveEvent()
    val userActivity: LiveData<UserActivityData?> = _userActivity

    private val _myVisitHistory: SingleLiveEvent<List<VisitHistoryContent>?> = SingleLiveEvent()
    val myVisitHistory: LiveData<List<VisitHistoryContent>?> = _myVisitHistory

    private val _myMedals: SingleLiveEvent<List<MyMedal>> = SingleLiveEvent()
    val myMedals: SingleLiveEvent<List<MyMedal>> = _myMedals

    val selectedMedal = Transformations.map(userActivity) { it?.medal }

    private val _allMedals: SingleLiveEvent<List<Medal>> = SingleLiveEvent()
    val allMedals: LiveData<List<Medal>> = _allMedals

    fun initAllMedals() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.getMedals()
            if (response.isSuccessful) {
                _allMedals.postValue(response.body()?.data ?: emptyList())
            }
        }
    }

    fun requestUserActivity() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.getUserActivity()
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
            val response = userDataSource.getMyVisitHistory(null, 20)
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
            val response = userDataSource.getMyMedals()
            _myMedals.postValue(_allMedals.value?.map { medal ->
                val hasMedal = (response.body()?.data
                    ?: emptyList()).find { it.medalId == medal.medalId } != null
                val isSelected = medal.medalId == _userActivity.value?.medal?.medalId
                MyMedal(medal, isSelected, hasMedal)
            } ?: emptyList())
        }
    }

    fun changeMedal(medalId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.updateMyMedals(UpdateMedalRequest(medalId))
            if (response.isSuccessful) {
                requestUserActivity()
            } else {
                _msgTextId.postValue(R.string.error_change_medal)
                _msgTextId.postValue(-1)
            }
        }
    }
}
