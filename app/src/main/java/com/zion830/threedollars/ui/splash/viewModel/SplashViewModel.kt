package com.zion830.threedollars.ui.splash.viewModel

import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.common.utils.SharedPrefUtils.Companion.BOSS_FEED_BACK_LIST
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.login.data.AccessCheckModel
import com.threedollar.domain.login.repository.LoginRepository
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.datasource.AppStatusRepository
import com.zion830.threedollars.datasource.model.AppUpdateDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPrefUtils: SharedPrefUtils,
    private val loginRepository: LoginRepository,
    private val homeRepository: HomeRepository,
    private val appStatusRepository: AppStatusRepository
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.SPLASH

    private val _splashAdvertisement: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val splashAdvertisement = _splashAdvertisement.asStateFlow()

    private val _accessCheckModel: MutableStateFlow<AccessCheckModel?> = MutableStateFlow(null)
    val accessCheckModel = _accessCheckModel.asStateFlow()

    private val _shouldShowUpdateDialog = MutableStateFlow<AppUpdateDialog?>(null)
    val shouldShowUpdateDialog = _shouldShowUpdateDialog.asStateFlow()

    private val currentVersion by lazy {
        GlobalApplication.instance.packageManager
            .getPackageInfo(GlobalApplication.instance.packageName, 0)
            .versionName
            .orEmpty()
    }

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            loginRepository.getFeedbackTypes(targetType = BOSS_STORE).collect {
                if (it.ok) {
                    withContext(Dispatchers.Main) {
                        sharedPrefUtils.saveList(it.data, BOSS_FEED_BACK_LIST)
                    }
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun getStoreMarkerAdvertisements(latLng: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {

            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.STORE_MARKER,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect {
                if (it.ok) {
                    val advertisements = it.data.orEmpty()
                    if (advertisements.isNotEmpty()) {
                        GlobalApplication.storeMarker = advertisements.first()
                    }
                }
            }
        }
    }

    fun getSplashAdvertisements(latLng: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {

            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.LOADING,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect {
                if (it.ok) {
                    val advertisements = it.data.orEmpty()
                    if (advertisements.isNotEmpty()) {
                        _splashAdvertisement.value = advertisements.first()
                    }
                }
            }
        }
    }

    fun checkAccessToken() {
        viewModelScope.launch(coroutineExceptionHandler) {
            loginRepository.getUserInfo().collect {
                _accessCheckModel.value = it
            }
        }
    }

    fun putPushInformation(informationRequest: PushInformationRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            loginRepository.putPushInformation(informationRequest)
        }
    }

    fun checkForceUpdate() {
        viewModelScope.launch(coroutineExceptionHandler) {
            appStatusRepository.getMinimumVersion().collect { updateDialog ->
                updateDialog.data?.let {
                    if (it.enabled) {
                        _shouldShowUpdateDialog.emit(it)
                    } else {
                        checkAccessToken()
                    }
                }
            }
        }
    }
}
