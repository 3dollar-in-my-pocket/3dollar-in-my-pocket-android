package com.zion830.threedollars.ui.dialog.category

import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.UdfViewModel
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
    private val storeDataSource: StoreDataSource
) : UdfViewModel<SelectCategoryIntent, SelectCategoryState, SelectCategoryEffect>() {

    private val categories = MutableStateFlow<ImmutableList<StoreCategory>>(persistentListOf())

    override val state: StateFlow<SelectCategoryState> = categories.mapLatest {
        SelectCategoryState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SelectCategoryState.Idle
    )

    private val _effect = Channel<SelectCategoryEffect>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val effect: Flow<SelectCategoryEffect> = _effect.receiveAsFlow()

    override fun dispatch(intent: SelectCategoryIntent) {
        when (intent) {
            is SelectCategoryIntent.OnInit -> {
                onInit()
            }
        }
    }

    private fun onInit() {
        if (state.value !is SelectCategoryState.Idle) {
            return
        }

        launchIfIdle(
            tag = "onInit"
        ) {
            storeDataSource.getCategories().collect {
                val ret = it.body()
                if (ret != null) {
                    categories.value = ret.toStoreCategories()
                } else {
                    _effect.send(SelectCategoryEffect.OnInitError)
                }
            }
        }
    }

    private fun CategoriesResponse.toStoreCategories(): ImmutableList<StoreCategory> = data.groupBy {
        it.classification
    }.mapValues { entry ->
        StoreCategory(
            classification = StoreCategoryClassification(
                type = entry.key.type,
                name = entry.key.description,
                priority = entry.key.priority
            ),
            items = entry.value.map { value ->
                StoreCategoryItem(
                    id = value.categoryId,
                    name = value.name,
                    description = value.description,
                    imageUrl = value.imageUrl,
                    disableImageUrl = value.disableImageUrl,
                    isNew = value.isNew
                )
            }
        )
    }.values.sortedBy {
        it.classification.priority
    }.toImmutableList()
}
