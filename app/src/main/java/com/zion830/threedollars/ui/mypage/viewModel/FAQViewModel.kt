package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.datasource.model.v2.response.FAQCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class FAQViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.FAQ

    val faqTags: LiveData<List<FAQCategory>> = liveData(Dispatchers.IO + coroutineExceptionHandler) {
        val result = userDataSource.getFAQCategory()
        if (result.isSuccessful) {
            emit(result.body()?.data ?: emptyList())
        }
    }

    private val _faqsByTag: MutableLiveData<FAQByCategoryResponse> = MutableLiveData()
    val faqsByTag: LiveData<FAQByCategoryResponse>
        get() = _faqsByTag

    fun loadFaqs(category: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = userDataSource.getFAQList(category)
            if (data.isSuccessful) {
                _faqsByTag.postValue(data.body())
            } else {
                _msgTextId.postValue(CommonR.string.connection_failed)
            }
        }
    }
}