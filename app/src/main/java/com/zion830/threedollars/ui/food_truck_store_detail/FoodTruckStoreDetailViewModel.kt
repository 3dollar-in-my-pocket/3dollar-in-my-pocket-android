package com.zion830.threedollars.ui.food_truck_store_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreDetailModel
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackFullResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import zion830.com.common.base.BaseResponse
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class FoodTruckStoreDetailViewModel @Inject constructor(private val repository: StoreDataSource) :
    BaseViewModel() {

    private val _bossStoreDetailModel = SingleLiveEvent<BossStoreDetailModel>()
    val bossStoreDetailModel: LiveData<BossStoreDetailModel> get() = _bossStoreDetailModel

    private val _bossStoreFeedbackFullModelList =
        SingleLiveEvent<List<BossStoreFeedbackFullResponse.BossStoreFeedbackFullModel>>()
    val bossStoreFeedbackFullModelList: LiveData<List<BossStoreFeedbackFullResponse.BossStoreFeedbackFullModel>> get() = _bossStoreFeedbackFullModelList

    private val _postFeedbackResponse = SingleLiveEvent<Response<BaseResponse<String>>>()
    val postFeedbackResponse: LiveData<Response<BaseResponse<String>>> get() = _postFeedbackResponse

    fun getFoodTruckStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            repository.getBossStoreDetail(
                bossStoreId = bossStoreId,
                latitude = latitude,
                longitude = longitude
            ).body()?.data?.let {
                _bossStoreDetailModel.value = it
            }
        }
    }

    fun getBossStoreFeedbackFull(bossStoreId: String) {
        viewModelScope.launch {
            repository.getBossStoreFeedbackFull(bossStoreId).body()?.data?.let {
                _bossStoreFeedbackFullModelList.value = it
            }
        }
    }

    fun postBossStoreFeedback(
        bossStoreId: String,
        bossStoreFeedbackRequest: BossStoreFeedbackRequest
    ) {
        viewModelScope.launch {
            _postFeedbackResponse.value =
                repository.postBossStoreFeedback(bossStoreId, bossStoreFeedbackRequest)
        }
    }
}