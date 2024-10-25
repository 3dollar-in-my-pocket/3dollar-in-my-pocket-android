package com.my.presentation.page

import androidx.lifecycle.viewModelScope
import com.my.domain.repository.MyRepository
import com.my.presentation.page.data.MyPageShop
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.listener.MyFragments
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.data.poll.response.GetMyPollListResponse
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.data.visit_history.MyVisitHistoryResponseV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val myRepository: MyRepository) : BaseViewModel() {

    private val _userInfo = MutableSharedFlow<UserWithDetailApiResponse>(replay = 1)
    val userInfo: SharedFlow<UserWithDetailApiResponse> = _userInfo
    private val _myFavoriteStores = MutableSharedFlow<MyFavoriteFolderResponse>()
    val myFavoriteStores: SharedFlow<MyFavoriteFolderResponse> = _myFavoriteStores
    private val _myVisitsStore = MutableSharedFlow<MyVisitHistoryResponseV2>()
    val myVisitsStore: SharedFlow<MyVisitHistoryResponseV2> = _myVisitsStore
    private val _userPollList = MutableSharedFlow<GetMyPollListResponse>()
    val userPollList: SharedFlow<GetMyPollListResponse> = _userPollList
    private val _addFragments = MutableSharedFlow<MyFragments>()
    val addFragments: SharedFlow<MyFragments> = _addFragments
    private val _favoriteClick = MutableSharedFlow<Unit>()
    val favoriteClick: SharedFlow<Unit> = _favoriteClick
    private val _teamClick = MutableSharedFlow<Unit>()
    val teamClick: SharedFlow<Unit> = _teamClick
    private val _storeClick = MutableSharedFlow<MyPageShop>()
    val storeClick: SharedFlow<MyPageShop> = _storeClick

    var isMoveMedalPage = false

    fun getUserInfo() = viewModelScope.launch(coroutineExceptionHandler) {
        myRepository.getUserInfo().collect {
            if (it.ok) {
                it.data?.let { data ->
                    _userInfo.emit(data.copy())
                }
            }
        }
    }

    fun getMyVisitsStore() = viewModelScope.launch(coroutineExceptionHandler) {
        myRepository.getMyVisitsStore().collect {
            if (it.ok) {
                it.data?.let { data ->
                    _myVisitsStore.emit(data)
                }
            }
        }
    }

    fun getMyFavoriteStores() = viewModelScope.launch(coroutineExceptionHandler) {
        myRepository.getMyFavoriteStores().collect {
            if (it.ok) {
                it.data?.let { data ->
                    _myFavoriteStores.emit(data)
                }
            }
        }
    }

    fun getUserPollList() = viewModelScope.launch(coroutineExceptionHandler) {
        myRepository.getUserPollList(null, size = 3).collect {
            if (it.ok) {
                it.data?.let { data ->
                    _userPollList.emit(data)
                }
            }
        }
    }

    fun addFragments(myFragments: MyFragments) = viewModelScope.launch(coroutineExceptionHandler) {
        _addFragments.emit(myFragments)
    }

    fun clickFavorite() = viewModelScope.launch(coroutineExceptionHandler) {
        _favoriteClick.emit(Unit)
    }

    fun clickStore(myPageShop: MyPageShop) = viewModelScope.launch(coroutineExceptionHandler) {
        _storeClick.emit(myPageShop)
    }

    fun clickTeam() = viewModelScope.launch(coroutineExceptionHandler) {
        _teamClick.emit(Unit)
    }

    fun isNameUpdated() = getUserInfo()
}