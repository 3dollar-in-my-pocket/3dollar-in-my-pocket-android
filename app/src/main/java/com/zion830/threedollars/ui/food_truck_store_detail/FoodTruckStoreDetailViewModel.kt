package com.zion830.threedollars.ui.food_truck_store_detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.BossStoreDetailModel
import com.home.domain.data.store.FavoriteModel
import com.home.domain.data.store.FoodTruckReviewModel
import com.home.domain.repository.HomeRepository
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.Constants.BOSS_STORE
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.utils.StringUtils.getString
import com.zion830.threedollars.utils.showCustomBlackToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FoodTruckStoreDetailViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BaseViewModel() {

    private val _bossStoreDetailModel = MutableStateFlow(BossStoreDetailModel())
    val bossStoreDetailModel: StateFlow<BossStoreDetailModel> get() = _bossStoreDetailModel

    private val _foodTruckReviewModelList: MutableStateFlow<List<FoodTruckReviewModel>> = MutableStateFlow(listOf())
    val foodTruckReviewModelList: StateFlow<List<FoodTruckReviewModel>> get() = _foodTruckReviewModelList

    private val _postFeedback: MutableStateFlow<BaseResponse<String>?> = MutableStateFlow(null)
    val postFeedback: StateFlow<BaseResponse<String>?> get() = _postFeedback

    private val _favoriteModel: MutableStateFlow<FavoriteModel> = MutableStateFlow(FavoriteModel())
    val favoriteModel: StateFlow<FavoriteModel> get() = _favoriteModel

    fun getFoodTruckStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getBossStoreDetail(bossStoreId = bossStoreId, deviceLatitude = latitude, deviceLongitude = longitude).collect {
                _bossStoreDetailModel.value = it.data
                _favoriteModel.value = it.data.favoriteModel
            }
        }
    }

    fun getBossStoreFeedbackFull(bossStoreId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getFeedbackFull(targetType = BOSS_STORE, targetId = bossStoreId).collect {
                _foodTruckReviewModelList.value = it.data
            }
        }
    }

    fun postBossStoreFeedback(bossStoreId: String, bossStoreFeedbackRequest: List<String>) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postFeedback(BOSS_STORE, bossStoreId, bossStoreFeedbackRequest).collect {
                _postFeedback.value = it
            }
        }
    }

    fun putFavorite(storeType: String, storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putFavorite(storeType, storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(getString(R.string.toast_favorite_add))
                    _favoriteModel.update {
                        it.copy(isFavorite = true, totalSubscribersCount = it.totalSubscribersCount + 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }

    fun deleteFavorite(storeType: String, storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteFavorite(storeType, storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(getString(R.string.toast_favorite_delete))
                    _favoriteModel.update {
                        it.copy(isFavorite = false, totalSubscribersCount = it.totalSubscribersCount - 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }
}