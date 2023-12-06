package com.threedollar.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.data.Category
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PopularStore
import com.threedollar.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(private val communityRepository: CommunityRepository) : BaseViewModel() {

    private val _categoryList: MutableSharedFlow<List<Category>> = MutableStateFlow(listOf())
    val categoryList: SharedFlow<List<Category>> = _categoryList.asSharedFlow()

    private val _neighborhoods: MutableStateFlow<Neighborhoods> = MutableStateFlow(Neighborhoods(listOf()))
    val neighborhoods: StateFlow<Neighborhoods> = _neighborhoods.asStateFlow()

    private val _popularStores: MutableSharedFlow<List<PopularStore>> = MutableSharedFlow()
    val popularStores: SharedFlow<List<PopularStore>> = _popularStores.asSharedFlow()

    private val _pollItems: MutableSharedFlow<List<PollItem>> = MutableSharedFlow()
    val pollItems: SharedFlow<List<PollItem>> = _pollItems.asSharedFlow()

    private val _pollSelected: MutableSharedFlow<Pair<String, String>> = MutableSharedFlow()
    val pollSelected: SharedFlow<Pair<String, String>> = _pollSelected.asSharedFlow()

    private val _toast: MutableSharedFlow<String> = MutableSharedFlow()
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    init {
        getPollCategories()
        getNeighborhoods()
    }

    private fun getPollCategories() {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollCategories().collect {
                if (it.ok) _categoryList.emit(it.data.orEmpty())
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun getPollItems(categoryId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollList(categoryId, "POPULAR", "").collect {
                if (it.ok) _pollItems.emit(it.data?.pollItems.orEmpty())
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun votePoll(pollId: String, optionId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.putPollChoice(pollId, optionId).collect {
                if (it.ok) _pollSelected.emit(Pair(pollId, optionId))
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun getPopularStores(criteria: String, district: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPopularStores(criteria, district, "").collect {
                if (it.ok) _popularStores.emit(it.data?.content.orEmpty())
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    private fun getNeighborhoods() {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getNeighborhoods().collect {
                if (it.ok) _neighborhoods.value = it.data!!
                else _toast.emit(it.message.orEmpty())
            }
        }
    }
}

sealed class PopularStoreCriteria(val type: String) {
    object MostReview : PopularStoreCriteria("MOST_REVIEWS")
    object MostVisits : PopularStoreCriteria("MOST_VISITS")
}

@Suppress("UNCHECKED_CAST")
suspend fun <T : Any> PagingData<T>.toList(): List<T> {
    val flow = PagingData::class.java.getDeclaredField("flow").apply {
        isAccessible = true
    }.get(this) as Flow<Any?>
    val pageEventInsert = flow.single()
    val pageEventInsertClass = Class.forName("androidx.paging.PageEvent\$Insert")
    val pagesField = pageEventInsertClass.getDeclaredField("pages").apply {
        isAccessible = true
    }
    val pages = pagesField.get(pageEventInsert) as List<Any?>
    val transformablePageDataField =
        Class.forName("androidx.paging.TransformablePage").getDeclaredField("data").apply {
            isAccessible = true
        }
    val listItems =
        pages.flatMap { transformablePageDataField.get(it) as List<*> }
    return listItems as List<T>
}