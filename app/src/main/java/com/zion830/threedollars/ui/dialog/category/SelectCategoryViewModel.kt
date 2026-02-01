package com.zion830.threedollars.ui.dialog.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.repository.HomeRepository
import com.zion830.threedollars.const.ArgumentKey
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
    private val storeDataSource: StoreDataSource,
    private val homeRepository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : UdfViewModel<SelectCategoryIntent, SelectCategoryState, SelectCategoryEffect>() {

    private val location = savedStateHandle.get<LatLng>(ArgumentKey.LOCATION) ?: LatLng.INVALID

    private val categories = MutableStateFlow<ImmutableList<StoreCategory>>(persistentListOf())
    private val categoryBannerAd = MutableStateFlow<AdvertisementModelV2?>(null)
    private val categoryIconAd = MutableStateFlow<AdvertisementModelV2?>(null)

    override val state: StateFlow<SelectCategoryState> = combine(
        categories,
        categoryBannerAd,
        categoryIconAd
    ) { categories, categoryBannerAd, categoryIconAd ->
        SelectCategoryState.Success(
            bannerAd = categoryBannerAd,
            categories = categoryIconAd?.let { categories.injectAd(it) } ?: categories
        )
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

            is SelectCategoryIntent.OnCategoryClick -> {
                handleOnCategoryClick(intent)
            }

            is SelectCategoryIntent.OnCategoryAdBannerClick -> {
                handleOnCategoryAdBannerClick(intent)
            }
        }
    }

    private fun onInit() {
        if (state.value !is SelectCategoryState.Idle) {
            return
        }

        init()
    }

    private fun init() {
        launchIfIdle(
            tag = "init"
        ) {
            launch {
                loadCategories()
            }

            launch {
                loadCategoryIconAdItem()
            }

            launch {
                loadCategoryAdBanner()
            }
        }
    }

    private fun handleOnCategoryClick(
        intent: SelectCategoryIntent.OnCategoryClick
    ) {
        when (intent.item) {
            is StoreCategoryItem.Food -> {
                launch {
                    _effect.send(SelectCategoryEffect.ChangeFoodCategory(intent.item))
                }
            }

            is StoreCategoryItem.Ad -> {
                launch {
                    LogManager.sendEvent(ClickEvent(
                        screen = ScreenName.CATEGORY_FILTER,
                        objectType = LogObjectType.BUTTON,
                        objectId = LogObjectId.ADVERTISEMENT,
                        additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to intent.item.data)
                    ))

                    _effect.send(SelectCategoryEffect.HandleAd(intent.item.data))
                }
            }
        }
    }

    private fun handleOnCategoryAdBannerClick(
        intent: SelectCategoryIntent.OnCategoryAdBannerClick
    ) {
        launch {
            LogManager.sendEvent(ClickEvent(
                screen = ScreenName.CATEGORY_FILTER,
                objectType = LogObjectType.BANNER,
                objectId = LogObjectId.ADVERTISEMENT,
                additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to intent.data.advertisementId)
            ))

            _effect.send(SelectCategoryEffect.HandleAd(intent.data))
        }
    }

    private suspend fun loadCategories() {
        storeDataSource.getCategories().collect {
            val ret = it.body()
            if (ret != null) {
                categories.value = ret.toFoodCategories()
            } else {
                _effect.send(SelectCategoryEffect.InitError)
            }
        }
    }

    private suspend fun loadCategoryIconAdItem() {
        homeRepository.getAdvertisements(
            position = AdvertisementsPosition.MENU_CATEGORY_ICON,
            deviceLatitude = location.latitude,
            deviceLongitude = location.longitude
        ).collect { ret ->
            categoryIconAd.update { ret.data?.firstOrNull() }
        }
    }

    private suspend fun loadCategoryAdBanner() {
        homeRepository.getAdvertisements(
            position = AdvertisementsPosition.MENU_CATEGORY_BANNER,
            deviceLatitude = location.latitude,
            deviceLongitude = location.longitude
        ).collect { ret ->
            categoryBannerAd.update { ret.data?.firstOrNull() }
        }
    }

    private fun CategoriesResponse.toFoodCategories(): ImmutableList<StoreCategory> = data.groupBy {
        it.classification
    }.mapValues { entry ->
        StoreCategory(
            classification = StoreCategoryClassification(
                type = entry.key.type,
                name = entry.key.description,
                priority = entry.key.priority
            ),
            items = entry.value.map { value ->
                StoreCategoryItem.Food(
                    id = value.categoryId,
                    name = value.name,
                    description = value.description,
                    imageUrl = value.imageUrl,
                    disableImageUrl = value.disableImageUrl,
                    isNew = value.isNew
                )
            }.toImmutableList()
        )
    }.values.sortedBy {
        it.classification.priority
    }.toImmutableList()

    private fun ImmutableList<StoreCategory>.injectAd(
        target: AdvertisementModelV2
    ): ImmutableList<StoreCategory> {
        val originList = this

        val first = originList.firstOrNull() ?: return this

        val targetIndex = target
            .metadata
            .exposureIndex.takeIf { first.items.lastIndex >= it } ?: first.items.lastIndex

        if (targetIndex < 0) {
            return this
        }

        val injected = buildList(first.items.size + 1) {
            addAll(first.items)
            add(targetIndex, StoreCategoryItem.Ad(target))
        }

        return buildList(originList.size) {
            add(first.copy(items = injected.toImmutableList()))

            if (originList.size > 1) {
                val subList = originList.subList(1, originList.size)
                addAll(subList)
            }

        }.toImmutableList()
    }
}
