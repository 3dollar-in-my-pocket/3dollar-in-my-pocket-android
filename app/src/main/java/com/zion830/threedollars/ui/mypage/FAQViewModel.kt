package com.zion830.threedollars.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.response.FaqByTagResponse
import com.zion830.threedollars.repository.model.response.FaqTag
import com.zion830.threedollars.repository.model.response.FaqTagResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class FAQViewModel : BaseViewModel() {

    private val repository = UserRepository()

    val faqTags: LiveData<FaqTagResponse> = liveData(Dispatchers.IO + coroutineExceptionHandler) {
        emit(repository.loadFaqTags())
    }

    private val _faqsByTag: MutableLiveData<FaqByTagResponse> = MutableLiveData()
    val faqsByTag: LiveData<FaqByTagResponse>
        get() = _faqsByTag

    fun loadFaqs(tags: ArrayList<FaqTag>) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val data = repository.loadFaqs(tags.map { it.id }.toIntArray())
            _faqsByTag.postValue(data)
        }
    }
}