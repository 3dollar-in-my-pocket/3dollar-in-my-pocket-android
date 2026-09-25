package com.zion830.threedollars.ui.storeDetail.user.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.ext.getMonthFirstDate
import com.threedollar.domain.home.data.store.UserStoreModel
import com.zion830.threedollars.utils.NaverMapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@HiltViewModel
class StoreCertificationViewModel @Inject constructor(private val homeRepository: HomeRepository) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.VISIT_STORE

    private val _storeVisitResult = MutableSharedFlow<Boolean>()
    val storeVisitResult: SharedFlow<Boolean> get() = _storeVisitResult

    private val _needUpdate = MutableLiveData<Boolean>()
    val needUpdate: LiveData<Boolean> get() = _needUpdate

    init {
        viewModelScope.launch {
            while (true) {
                _needUpdate.value = true
                delay(2000L)
            }
        }
    }

    private val _certificationStore = MutableSharedFlow<UserStoreModel?>(replay = 1)

    /** [loadCertificationStore] 결과. 조회에 실패하면 null 이다. */
    val certificationStore: SharedFlow<UserStoreModel?> get() = _certificationStore

    /**
     * 가게 id 만 받고 열렸을 때 인증 화면에 필요한 가게명·위치·카테고리를 조회한다 (iOS `VisitViewModel` 과 동일).
     */
    fun loadCertificationStore(storeId: Int, deviceLocation: LatLng?) {
        val location = deviceLocation ?: NaverMapUtils.DEFAULT_LOCATION
        viewModelScope.launch {
            runCatching {
                homeRepository.getUserStoreDetail(
                    storeId = storeId,
                    deviceLatitude = location.latitude,
                    deviceLongitude = location.longitude,
                    storeImagesCount = 0,
                    reviewsCount = 0,
                    visitHistoriesCount = 0,
                    filterVisitStartDate = getMonthFirstDate(),
                ).first()
            }.onSuccess { response ->
                if (!response.ok) _serverError.emit(response.message)
                _certificationStore.emit(response.data?.store?.takeIf { response.ok })
            }.onFailure {
                _certificationStore.emit(null)
            }
        }
    }

    fun postStoreVisit(storeId: Int, isExist: Boolean) {
        showLoading()
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postStoreVisit(storeId, if (isExist) "EXISTS" else "NOT_EXISTS").collect {
                _storeVisitResult.emit(it.ok)
                if (!it.ok) _serverError.emit(it.message)
                hideLoading()
            }
        }
    }

    // GA Events
    fun sendClickVisitSuccess() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.VISIT_SUCCESS
            )
        )
    }

    fun sendClickVisitFail() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.VISIT_FAIL
            )
        )
    }
}