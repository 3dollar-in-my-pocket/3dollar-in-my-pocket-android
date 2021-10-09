package com.zion830.threedollars.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.v2.response.FAQByCategoryResponse
import com.zion830.threedollars.repository.model.v2.response.FAQCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class FAQViewModel : BaseViewModel() {

    private val repository = UserRepository()

    val faqTags: LiveData<List<FAQCategory>> = liveData(Dispatchers.IO + coroutineExceptionHandler) {
        val result = repository.getFAQCategory()
        if (result.isSuccessful) {
            emit(result.body()?.data ?: emptyList())
        }
    }

    private val _faqsByTag: MutableLiveData<FAQByCategoryResponse> = MutableLiveData()
    val faqsByTag: LiveData<FAQByCategoryResponse>
        get() = _faqsByTag

    fun loadFaqs(category: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.getFAQList(category)
            if (data.isSuccessful) {
                _faqsByTag.postValue(data.body())
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }
}