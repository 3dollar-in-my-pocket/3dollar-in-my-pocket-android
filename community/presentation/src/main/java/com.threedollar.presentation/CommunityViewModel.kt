package com.threedollar.presentation

import androidx.lifecycle.viewModelScope
import com.threedollar.domain.data.Category
import com.threedollar.domain.data.Cursor
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.domain.data.PopularStores
import com.threedollar.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

class CommunityViewModel @Inject constructor(private val communityRepository: CommunityRepository) : BaseViewModel() {

    private val _categoryList: MutableStateFlow<List<Category>> = MutableStateFlow(listOf())
    val categoryList: StateFlow<List<Category>> = _categoryList

    private val _neighborhoods: MutableStateFlow<Neighborhoods> = MutableStateFlow(Neighborhoods(listOf()))
    val neighborhoods: StateFlow<Neighborhoods> = _neighborhoods

    private val _popularStores: MutableStateFlow<PopularStores> = MutableStateFlow(PopularStores(listOf(), Cursor("", false)))
    val popularStores: StateFlow<PopularStores> = _popularStores
    fun getPollCategories() {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollCategories().collect {
                _categoryList.value = it
            }
        }
    }

    fun getPopularStores() {

    }

    fun getNeighborhoods() {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getNeighborhoods().collect {
                _neighborhoods.value = it
            }
        }
    }
}