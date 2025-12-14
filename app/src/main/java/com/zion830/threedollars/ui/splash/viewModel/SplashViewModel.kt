package com.zion830.threedollars.ui.splash.viewModel

import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.repository.HomeRepository
import com.login.domain.data.AccessCheckModel
import com.login.domain.repository.LoginRepository
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.common.utils.SharedPrefUtils.Companion.BOSS_FEED_BACK_LIST
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.datasource.AppStatusRepository
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.AppUpdateDialog
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPrefUtils: SharedPrefUtils,
    private val loginRepository: LoginRepository,
    private val storeDataSource: StoreDataSource,
    private val homeRepository: HomeRepository,
    private val appStatusRepository: AppStatusRepository
) : BaseViewModel() {

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
            storeDataSource.getCategories().collect { categories ->
                if (categories.isSuccessful) {
                    val categoriesModelList = categories.body()?.data ?: emptyList()
                    LegacySharedPrefUtils.saveCategories(categoriesModelList.filter { it.classification.description == "간식류" })
                    LegacySharedPrefUtils.saveTruckCategories(categoriesModelList.filter { it.classification.description == "식사류" })
                } else {
                    _msgTextId.postValue(CommonR.string.connection_failed)
                }
            }
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
