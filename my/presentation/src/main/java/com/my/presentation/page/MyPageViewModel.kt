package com.my.presentation.page

import androidx.lifecycle.viewModelScope
import com.my.domain.repository.MyRepository
import com.my.domain.model.UserInfoModel
import com.my.domain.model.FavoriteStoresModel
import com.my.domain.model.VisitHistoryModel
import com.my.domain.model.UserPollsModel
import com.my.presentation.page.data.MyPageShop
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.listener.MyFragments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val myRepository: MyRepository) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.MY_PAGE

    /**
     * States
     */
    private val _userInfo = MutableStateFlow(value = UserInfoModel())
    val userInfo: StateFlow<UserInfoModel> =_userInfo.asStateFlow()

    private val _myFavoriteStores = MutableStateFlow(FavoriteStoresModel())
    val myFavoriteStores: StateFlow<FavoriteStoresModel> = _myFavoriteStores.asStateFlow()

    private val _myVisitsStore = MutableStateFlow(VisitHistoryModel())
    val myVisitsStore: StateFlow<VisitHistoryModel> = _myVisitsStore.asStateFlow()

    private val _userPollList = MutableStateFlow(UserPollsModel())
    val userPollList: StateFlow<UserPollsModel> = _userPollList.asStateFlow()

    /**
     * Events
     */
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
                    _userInfo.update { data }
                }
            }
        }
    }

    fun getMyVisitsStore() = viewModelScope.launch(coroutineExceptionHandler) {
        myRepository.getMyVisitsStore().collect {
            if (it.ok) {
                it.data?.let { data ->
                    _myVisitsStore.update { data }
                }
            }
        }
    }

    fun getMyFavoriteStores() = viewModelScope.launch(coroutineExceptionHandler) {
        myRepository.getMyFavoriteStores().collect {
            if (it.ok) {
                it.data?.let { data ->
                    _myFavoriteStores.update { data }
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

    // GA Events - MyPage
    fun sendClickVisitedStore() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.VISITED_STORE
            )
        )
    }

    fun sendClickFavoritedStore() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.FAVORITED_STORE
            )
        )
    }

    fun sendClickMedal() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.MEDAL
            )
        )
    }

    fun sendClickReview() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.REVIEW
            )
        )
    }
}