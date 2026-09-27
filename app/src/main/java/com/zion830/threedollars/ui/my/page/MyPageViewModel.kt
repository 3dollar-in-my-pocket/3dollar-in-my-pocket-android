package com.zion830.threedollars.ui.my.page

import androidx.lifecycle.viewModelScope
import com.threedollar.domain.my.repository.MyRepository
import com.threedollar.domain.my.model.UserInfoModel
import com.threedollar.domain.my.model.FavoriteStoresModel
import com.threedollar.domain.my.model.VisitHistoryModel
import com.threedollar.domain.my.model.UserPollsModel
import com.threedollar.domain.store.model.IssuedCouponModel
import com.threedollar.domain.store.model.IssuedCouponPageModel
import com.threedollar.domain.store.model.IssuedCouponStatus
import com.threedollar.domain.store.repository.StoreRepository
import com.zion830.threedollars.ui.my.page.data.MyPageShop
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.listener.MyFragments
import com.threedollar.common.analytics.ParameterName
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
class MyPageViewModel @Inject constructor(
    private val myRepository: MyRepository,
    private val storeRepository: StoreRepository,
) : BaseViewModel() {

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

    /** 마이페이지 쿠폰 섹션. 아직 조회 전이면 null. */
    private val _myCoupons = MutableStateFlow<IssuedCouponPageModel?>(null)
    val myCoupons: StateFlow<IssuedCouponPageModel?> = _myCoupons.asStateFlow()

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

    private val _couponClick = MutableSharedFlow<Unit>()
    val couponClick: SharedFlow<Unit> = _couponClick

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
    fun getMyCoupons() = viewModelScope.launch(coroutineExceptionHandler) {
        storeRepository.getMyIssuedCoupons(
            statuses = listOf(IssuedCouponStatus.ISSUED),
            cursor = null,
            size = MY_PAGE_COUPON_SIZE,
        ).onSuccess { page -> _myCoupons.update { page } }
    }

    fun clickCouponSection() = viewModelScope.launch {
        _couponClick.emit(Unit)
    }

    /** 쿠폰 카드는 기존 카드 클릭 이벤트(`my_page`/`card`/`store`)로 보낸다 (iOS 와 동일). */
    fun clickCoupon(coupon: IssuedCouponModel) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.STORE,
                additionalParams = mapOf(ParameterName.STORE_ID to coupon.storeId),
            )
        )
        clickCouponSection()
    }

    fun sendClickVisitedStore(storeId: String, storeType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.VISITED_STORE,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId,
                    ParameterName.STORE_TYPE to storeType
                )
            )
        )
    }

    fun sendClickFavoritedStore(storeId: String, storeType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.FAVORITED_STORE,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId,
                    ParameterName.STORE_TYPE to storeType
                )
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

    fun sendClickPoll() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.POLL
            )
        )
    }
}

private const val MY_PAGE_COUPON_SIZE = 20
