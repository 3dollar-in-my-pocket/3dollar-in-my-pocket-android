package com.zion830.threedollars.ui.food_truck_store_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.repository.model.v2.response.store.BossStoreDetailModel
import com.zion830.threedollars.repository.model.v2.response.store.BossStoreFeedbackFullResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import zion830.com.common.base.BaseResponse
import zion830.com.common.base.BaseViewModel

class FoodTruckStoreDetailViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    private val _bossStoreDetailModel = MutableLiveData<BossStoreDetailModel>()
    val bossStoreDetailModel: LiveData<BossStoreDetailModel> get() = _bossStoreDetailModel

    private val _bossStoreFeedbackFullModelList =
        MutableLiveData<List<BossStoreFeedbackFullResponse.BossStoreFeedbackFullModel>>()
    val bossStoreFeedbackFullModelList: LiveData<List<BossStoreFeedbackFullResponse.BossStoreFeedbackFullModel>> get() = _bossStoreFeedbackFullModelList

    private val _postFeedbackResponse = MutableLiveData<Response<BaseResponse<String>>>()
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
            _postFeedbackResponse.value = repository.postBossStoreFeedback(bossStoreId, bossStoreFeedbackRequest)
        }
    }
}