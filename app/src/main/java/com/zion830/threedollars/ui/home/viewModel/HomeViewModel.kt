package com.zion830.threedollars.ui.home.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.SDClickLogger
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.serverdriven.model.FilterOpenStatuses
import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterCurrentCategory
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.HomeFilterRadioOption
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.common.serverdriven.model.SDBorderModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreScreenModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.StoreModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.threedollar.domain.home.data.user.UserModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.threedollar.domain.screen.repository.ScreenRepository
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem
import com.zion830.threedollars.ui.home.data.HomeAroundStoreRequestParamsBuilder
import com.zion830.threedollars.ui.home.data.ChipAction
import com.zion830.threedollars.ui.home.data.HomeFilterCellType
import com.zion830.threedollars.ui.home.data.HomeListSectionQueryParamsBuilder
import com.zion830.threedollars.ui.home.data.HomeSortType
import com.zion830.threedollars.ui.home.data.HomeStoreType
import com.zion830.threedollars.ui.home.data.HomeUIState
import com.zion830.threedollars.ui.home.data.storePreviewStoreIdOrNull
import com.zion830.threedollars.ui.home.data.toFallbackStorePreviewScreen
import com.zion830.threedollars.utils.NaverMapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val screenRepository: ScreenRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.HOME

    private val _userInfo: MutableStateFlow<UserModel> = MutableStateFlow(UserModel())
    val userInfo: StateFlow<UserModel> get() = _userInfo

    private val _currentLocation: MutableStateFlow<LatLng> = MutableStateFlow(NaverMapUtils.DEFAULT_LOCATION)
    val currentLocation: StateFlow<LatLng> = _currentLocation.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState = _uiState.asStateFlow()

    private val _carouselUpdate = MutableSharedFlow<List<AdAndStoreItem>>(replay = 1)
    val carouselUpdate = _carouselUpdate.asSharedFlow()

    private val _filterCells = MutableStateFlow<List<HomeFilterCellType>>(emptyList())
    val filterCells: StateFlow<List<HomeFilterCellType>> = _filterCells.asStateFlow()

    private val _filterDeepLink = MutableSharedFlow<SDLinkModel>(extraBufferCapacity = 1)
    val filterDeepLink: SharedFlow<SDLinkModel> = _filterDeepLink.asSharedFlow()

    private val _homeListSection = MutableStateFlow(HomeListSectionModel())
    val homeListSection: StateFlow<HomeListSectionModel> = _homeListSection.asStateFlow()

    private val _selectedStoreScreen = MutableStateFlow<StoreScreenModel?>(null)
    val selectedStoreScreen: StateFlow<StoreScreenModel?> = _selectedStoreScreen.asStateFlow()

    private val _selectedStorePreviewStoreId = MutableStateFlow<Long?>(null)
    val selectedStorePreviewStoreId: StateFlow<Long?> = _selectedStorePreviewStoreId.asStateFlow()

    private val _storePreviewToast = MutableSharedFlow<String>()
    val storePreviewToast: SharedFlow<String> = _storePreviewToast.asSharedFlow()

    private val _selectedHomeListCardId = MutableStateFlow<String?>(null)
    val selectedHomeListCardId: StateFlow<String?> = _selectedHomeListCardId.asStateFlow()

    private var homeListNextCursor: String? = null
    private var isHomeListLoading = false

    private var shouldResetScroll = false

    fun consumeShouldResetScroll(): Boolean {
        val value = shouldResetScroll
        shouldResetScroll = false
        return value
    }

    private val _advertisementModel: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val advertisementModel: StateFlow<AdvertisementModelV2?> get() = _advertisementModel

    private val _advertisementListModel: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val advertisementListModel: StateFlow<AdvertisementModelV2?> get() = _advertisementListModel

    init {
        syncRadioSelectionFromLegacy()
        updateFilterCells()
        fetchHomeFilterScreen()
    }

    fun getUserInfo() {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getMyInfo().collect { response ->
                if (response.ok) {
                    _userInfo.value = response.data!!
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun updateCurrentLocation(latLng: LatLng) {
        _uiState.update { it.copy(userLocation = latLng) }
    }

    fun updateDistanceM(distanceM: Double) {
        _uiState.update { it.copy(currentDistanceM = distanceM) }
    }

    fun updateMapPosition(mapPosition: LatLng) {
        _uiState.update { it.copy(mapPosition = mapPosition) }
        savedStateHandle[KEY_MAP_POSITION] = mapPosition
    }

    fun getSavedMapPosition(): LatLng? {
        return savedStateHandle.get<LatLng>(KEY_MAP_POSITION)
    }

    fun updateUserLocation(latLng: LatLng) {
        _uiState.update { it.copy(userLocation = latLng) }
    }

    fun fetchAroundStores() {
        fetchAroundStores(uiState.value)
    }

    fun fetchAroundStores(
        mapPosition: LatLng,
        userLocation: LatLng = mapPosition,
    ) {
        val state = uiState.value.copy(
            mapPosition = mapPosition,
            userLocation = userLocation,
        )
        _uiState.value = state
        savedStateHandle[KEY_MAP_POSITION] = mapPosition
        fetchAroundStores(state)
    }

    private fun fetchAroundStores(state: HomeUIState) {
        fetchHomeListSection(state = state, cursor = null, append = false)
    }

    fun refreshHomeListSectionAfterStoreUpdate() {
        fetchHomeListSection(
            state = uiState.value,
            cursor = null,
            append = false,
            preserveSelectedStore = true,
        )
    }

    fun fetchNextHomeListSection() {
        val cursor = homeListNextCursor ?: return
        if (isHomeListLoading) return
        fetchHomeListSection(state = uiState.value, cursor = cursor, append = true)
    }

    private fun fetchHomeListSection(
        state: HomeUIState,
        cursor: String?,
        append: Boolean,
        preserveSelectedStore: Boolean = false,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            isHomeListLoading = true
            val previousSelectedCardId = _selectedHomeListCardId.value
            val previousSelectedStoreId = _selectedStorePreviewStoreId.value
            val params = HomeAroundStoreRequestParamsBuilder.build(
                state = state,
                bars = allBars(),
            )

            screenRepository.getHomeListSection(
                distanceM = params.distanceM,
                categoryIds = params.categoryIds,
                targetStores = params.targetStores,
                mapLatitude = params.mapLatitude,
                mapLongitude = params.mapLongitude,
                deviceLatitude = params.deviceLatitude,
                deviceLongitude = params.deviceLongitude,
                dynamicParams = HomeListSectionQueryParamsBuilder.build(params.dynamicParams),
                cursor = cursor,
            ).collect { response ->
                isHomeListLoading = false
                if (response.ok) {
                    val section = response.data ?: HomeListSectionModel()
                    val nextCards = if (append) {
                        _homeListSection.value.cards + section.cards
                    } else {
                        section.cards
                    }
                    _currentLocation.emit(state.mapPosition)
                    _homeListSection.value = section.copy(cards = nextCards)
                    homeListNextCursor = section.cursor?.nextCursor?.takeIf { section.cursor?.hasMore == true }
                    if (!append) {
                        val cards = section.cards.filterIsInstance<HomeListCardModel.BasicCard>()
                        _selectedHomeListCardId.value = if (preserveSelectedStore) {
                            cards.selectedCardIdAfterRefresh(
                                previousSelectedCardId = previousSelectedCardId,
                                previousSelectedStoreId = previousSelectedStoreId,
                            )
                        } else {
                            cards.firstOrNull()?.cardId
                        }
                    }
                    sendHomeListImpressionLogs(section.cards)
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun List<HomeListCardModel.BasicCard>.selectedCardIdAfterRefresh(
        previousSelectedCardId: String?,
        previousSelectedStoreId: Long?,
    ): String? {
        return firstOrNull { it.cardId == previousSelectedCardId }?.cardId
            ?: previousSelectedStoreId?.let { storeId ->
                firstOrNull { it.storePreviewStoreIdOrNull() == storeId }?.cardId
            }
            ?: firstOrNull()?.cardId
    }

    fun selectHomeListCard(card: HomeListCardModel.BasicCard) {
        _selectedHomeListCardId.value = card.cardId
        card.clickLog?.let { SDClickLogger.send(it) }
        val storeId = card.storePreviewStoreIdOrNull()
        fetchStoreScreen(storeId, fallbackCard = card)
    }

    fun sendClickHomeListCard(card: HomeListCardModel.BasicCard) {
        card.clickLog?.let { SDClickLogger.send(it) }
    }

    fun selectHomeListMarker(card: HomeListCardModel.BasicCard) {
        _selectedHomeListCardId.value = card.cardId
        card.marker.clickLog?.let { SDClickLogger.send(it) }
        val storeId = card.storePreviewStoreIdOrNull()
        fetchStoreScreen(storeId, fallbackCard = card)
    }

    fun fetchStoreScreen(storeId: Long?, fallbackCard: HomeListCardModel.BasicCard? = null) {
        if (storeId == null) {
            return
        }
        _selectedStorePreviewStoreId.value = storeId
        fallbackCard?.toFallbackStorePreviewScreen()?.let { fallbackScreen ->
            _selectedStoreScreen.value = fallbackScreen
        }
        val state = uiState.value
        viewModelScope.launch(coroutineExceptionHandler) {
            screenRepository.getStoreScreen(
                storeId = storeId,
                deviceLatitude = state.userLocation.latitude,
                deviceLongitude = state.userLocation.longitude,
            ).collect { response ->
                if (response.ok) {
                    val screen = response.data ?: StoreScreenModel()
                    _selectedStoreScreen.value = screen
                    screen.viewLog?.let { SDClickLogger.send(it) }
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun closeStorePreview() {
        _selectedStoreScreen.value = null
        _selectedStorePreviewStoreId.value = null
    }

    fun refreshSelectedStorePreview() {
        fetchStoreScreen(_selectedStorePreviewStoreId.value)
    }

    fun sendStorePreviewActionLog(actionBar: StoreActionBarModel) {
        actionBar.clickLog?.let { SDClickLogger.send(it) }
    }

    fun putFavoriteFromStorePreview(storeId: Long? = _selectedStorePreviewStoreId.value) {
        val targetStoreId = storeId ?: return
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putFavorite(targetStoreId.toString()).collect { response ->
                if (response.ok) {
                    updateStorePreviewFavorite(isFavorite = true)
                    _storePreviewToast.emit("가게를 저장했어요")
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun deleteFavoriteFromStorePreview(storeId: Long? = _selectedStorePreviewStoreId.value) {
        val targetStoreId = storeId ?: return
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteFavorite(targetStoreId.toString()).collect { response ->
                if (response.ok) {
                    updateStorePreviewFavorite(isFavorite = false)
                    _storePreviewToast.emit("가게 저장을 취소했어요")
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun updateSelectedStorePreviewFavorite(isFavorite: Boolean) {
        updateStorePreviewFavorite(isFavorite)
    }

    private fun updateStorePreviewFavorite(isFavorite: Boolean) {
        _selectedStoreScreen.update { screen ->
            screen?.copy(
                sections = screen.sections.map { section ->
                    if (section is StoreSectionModel.Preview) {
                        section.copy(
                            additionalInfos = section.additionalInfos.copy(isSubscriber = isFavorite),
                        )
                    } else {
                        section
                    }
                },
            )
        }
    }

    fun putPushInformation(pushToken: String, isMarketing: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putPushInformation(pushToken).collect { response ->
                if (response.ok) {
                    putMarketingConsent(if (isMarketing) "APPROVE" else "DENY")
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun changeSelectCategory(selected: StoreCategoryItem?) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.CATEGORY_FILTER,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.CATEGORY,
                additionalParams = selected?.id?.let {
                    mapOf(ParameterName.CATEGORY_ID to it)
                } ?: emptyMap()
            )
        )

        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update { it.copy(selectedCategory = selected) }
            fetchAroundStores()
            updateFilterCells()
        }
    }

    fun updateHomeFilterEvent(
        homeSortType: HomeSortType? = null,
        homeStoreType: HomeStoreType? = null,
        filterConditionsType: List<FilterConditionsTypeModel>? = null,
        filterCertifiedStores: Boolean? = null,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update {
                it.copy(
                    homeStoreType = homeStoreType ?: it.homeStoreType,
                    homeSortType = homeSortType ?: it.homeSortType,
                    filterConditionsType = filterConditionsType ?: it.filterConditionsType,
                    filterCertifiedStores = filterCertifiedStores ?: it.filterCertifiedStores,
                )
            }
            syncRadioSelectionFromLegacy()
            fetchAroundStores()
            updateFilterCells()
        }
    }

    fun updateFilterCondition(
        type: FilterConditionsTypeModel
    ) {
        val current = uiState.value.filterConditionsType
        val contains = current.contains(type)

        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.RECENT_ACTIVITY_FILTER,
                additionalParams = mapOf(ParameterName.VALUE to contains.toString())
            )
        )

        updateHomeFilterEvent(
            filterConditionsType = if (contains) {
                current.minus(type)
            } else {
                current.plus(type)
            }
        )
    }

    fun getAdvertisement(latLng: LatLng) {
        getAdvertisementList(latLng = latLng)
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.MAIN_PAGE_CARD,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect { response ->
                if (response.ok) {
                    _advertisementModel.value = response.data?.firstOrNull()
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun getAdvertisementList(latLng: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.STORE_LIST,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect { response ->
                if (response.ok) {
                    _advertisementListModel.value = response.data?.firstOrNull()
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun putMarketingConsent(marketingConsent: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putMarketingConsent(marketingConsent).collect { response ->
                if (response.ok) {
                    getUserInfo()
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    private fun updateCarouselItemList(itemList: List<AdAndStoreItem>) {
        viewModelScope.launch {
            shouldResetScroll = true
            _carouselUpdate.emit(itemList)
        }
    }

    private fun sendHomeListImpressionLogs(cards: List<HomeListCardModel>) {
        cards.forEach { card ->
            when (card) {
                is HomeListCardModel.BasicCard -> card.impressionLog?.let { SDClickLogger.send(it) }
                is HomeListCardModel.AdMobCard -> card.impressionLog?.let { SDClickLogger.send(it) }
                is HomeListCardModel.EmptyCard -> Unit
            }
        }
    }

    fun updateStoreItem(userStore: UserStoreModel) {
        val currentList = _carouselUpdate.replayCache.firstOrNull()?.toMutableList() ?: return
        val index = currentList.indexOfFirst { item ->
            (item as? ContentModel)?.storeModel?.storeId == userStore.storeId.toString()
        }

        if (index != -1) {
            val item = currentList[index] as ContentModel
            val updatedStoreModel = item.storeModel.copy(
                storeName = userStore.name,
                categories = userStore.categories,
                locationModel = userStore.location,
            )
            currentList[index] = item.copy(storeModel = updatedStoreModel)
            viewModelScope.launch {
                _carouselUpdate.emit(currentList)
            }
        }
    }

    // ----- SDU filter -----

    private fun fetchHomeFilterScreen() {
        viewModelScope.launch(coroutineExceptionHandler) {
            screenRepository.getHomeFilterScreen().collect { response ->
                if (response.ok && response.data != null) {
                    val sections = response.data!!.sections
                    _uiState.update {
                        it.copy(
                            filterSections = sections,
                            hasLoadedFilterScreen = true,
                        )
                    }
                    initializeRadioSelectionDefaults()
                    syncRadioSelectionFromLegacy()
                    updateFilterCells()
                } else {
                    _filterCells.value = makeFallbackFilterCells(uiState.value.selectedCategory)
                }
            }
        }
    }

    fun selectRadioOption(paramKey: String, optionIndex: Int) {
        val radioBar = allBars().firstOrNull {
            (it as? HomeFilterBar.RadioBar)?.paramKey == paramKey
        } as? HomeFilterBar.RadioBar ?: return
        val option = radioBar.options.getOrNull(optionIndex) ?: return
        val previousState = uiState.value

        _uiState.update { state ->
            val newSelection = state.radioSelection.toMutableMap()
            newSelection[paramKey] = optionIndex
            state.copy(radioSelection = newSelection)
        }
        applyParamValueToLegacyState(paramKey = paramKey, paramValue = option.paramValue)
        sendClickFilterLog(paramKey = paramKey, option = option, previousState = previousState)
        fetchAroundStores()
        updateFilterCells()
    }

    fun handleActionLink(link: SDLinkModel) {
        sendActionBarClickLog(link)
        viewModelScope.launch { _filterDeepLink.emit(link) }
    }

    fun closeSelectedCategory() {
        sendCurrentCategoryCloseLog()
        changeSelectCategory(null)
    }

    private fun allBars(): List<HomeFilterBar> {
        val serverBars = uiState.value.filterSections
            .filterIsInstance<HomeScreenSection.HomeFilterSectionModel>()
            .flatMap { it.bars }
        return serverBars.ifEmpty { fallbackBars() }
    }

    private fun initializeRadioSelectionDefaults() {
        val newSelection = uiState.value.radioSelection.toMutableMap()
        for (bar in allBars()) {
            if (bar is HomeFilterBar.RadioBar && newSelection[bar.paramKey] == null) {
                newSelection[bar.paramKey] = 0
                bar.options.firstOrNull()?.let { firstOption ->
                    applyParamValueToLegacyState(paramKey = bar.paramKey, paramValue = firstOption.paramValue)
                }
            }
        }
        _uiState.update { it.copy(radioSelection = newSelection) }
    }

    private fun syncRadioSelectionFromLegacy() {
        val state = uiState.value
        val newSelection = state.radioSelection.toMutableMap()
        for (bar in allBars()) {
            if (bar is HomeFilterBar.RadioBar) {
                val target = legacyParamValue(bar.paramKey, state)
                val matchedIndex = bar.options.indexOfFirst { it.paramValue == target }
                if (matchedIndex >= 0) {
                    newSelection[bar.paramKey] = matchedIndex
                }
            }
        }
        _uiState.update { it.copy(radioSelection = newSelection) }
    }

    private fun applyParamValueToLegacyState(paramKey: String, paramValue: String?) {
        when (paramKey) {
            "filterOpenStatuses" -> {
                val statuses = paramValue?.let { value ->
                    val parsed = FilterOpenStatuses.fromRaw(value)
                    if (parsed == FilterOpenStatuses.UNKNOWN) null else listOf(parsed)
                }
                _uiState.update { it.copy(openStatuses = statuses) }
            }
            "filterConditions" -> {
                val list = if (paramValue != null) {
                    listOf(FilterConditionsTypeModel.RECENT_ACTIVITY)
                } else {
                    emptyList()
                }
                _uiState.update { it.copy(filterConditionsType = list) }
            }
            "sortType" -> {
                val sort = paramValue?.let { runCatching { HomeSortType.valueOf(it) }.getOrNull() } ?: HomeSortType.DISTANCE_ASC
                _uiState.update { it.copy(homeSortType = sort) }
            }
            "targetStores" -> {
                val storeType = if (paramValue == "BOSS_STORE") HomeStoreType.BOSS_STORE else HomeStoreType.ALL
                _uiState.update { it.copy(homeStoreType = storeType) }
            }
        }
    }

    private fun legacyParamValue(paramKey: String, state: HomeUIState): String? = when (paramKey) {
        "filterOpenStatuses" -> state.openStatuses?.firstOrNull()?.name
        "filterConditions" -> if (state.filterConditionsType.contains(FilterConditionsTypeModel.RECENT_ACTIVITY)) "RECENT_ACTIVITY" else null
        "sortType" -> state.homeSortType.name
        "targetStores" -> if (state.homeStoreType == HomeStoreType.BOSS_STORE) "BOSS_STORE" else null
        else -> null
    }

    private fun updateFilterCells() {
        val state = uiState.value
        _filterCells.value = if (state.hasLoadedFilterScreen) {
            flattenFilterCells(state)
        } else {
            makeFallbackFilterCells(state.selectedCategory)
        }
    }

    private fun flattenFilterCells(state: HomeUIState): List<HomeFilterCellType> {
        return flattenFilterCells(state = state, bars = allBars())
    }

    private fun flattenFilterCells(
        state: HomeUIState,
        bars: List<HomeFilterBar>,
    ): List<HomeFilterCellType> {
        val cells = mutableListOf<HomeFilterCellType>()
        for (bar in bars) {
            when (bar) {
                is HomeFilterBar.CategoryBar -> {
                    cells.add(HomeFilterCellType.Chip(bar.categoriesFilter, ChipAction.OpenCategoryFilter))
                    state.selectedCategory?.let { category ->
                        val fontColor = bar.currentCategoryFilter?.fontColor ?: "#000000"
                        val chip = makeSelectedCategoryChip(category, fontColor)
                        cells.add(HomeFilterCellType.SelectedCategoryChip(chip, bar.currentCategoryFilter))
                    }
                }
                is HomeFilterBar.RadioBar -> {
                    if (bar.options.isEmpty()) continue
                    val selectedIndex = state.radioSelection[bar.paramKey] ?: 0
                    val currentOption = bar.options.getOrNull(selectedIndex) ?: continue
                    val nextIndex = (selectedIndex + 1) % bar.options.size
                    cells.add(
                        HomeFilterCellType.Chip(
                            currentOption.chip,
                            ChipAction.SelectRadio(bar.paramKey, nextIndex),
                        )
                    )
                }
                is HomeFilterBar.ActionBar -> {
                    cells.add(HomeFilterCellType.Button(bar.button, bar.clickLog))
                }
                is HomeFilterBar.Unknown -> Unit
            }
        }
        return cells
    }

    private fun makeFallbackFilterCells(selectedCategory: StoreCategoryItem?): List<HomeFilterCellType> {
        return flattenFilterCells(
            state = uiState.value.copy(selectedCategory = selectedCategory),
            bars = fallbackBars(),
        )
    }

    private fun fallbackBars(): List<HomeFilterBar> = listOf(
        HomeFilterBar.CategoryBar(
            type = HomeFilterBarType.CATEGORY_BAR,
            categoriesFilter = fallbackChip(text = "음식 종류"),
            categoriesFilterClickLog = null,
            currentCategoryFilter = HomeFilterCurrentCategory(
                fontColor = "#FF858F",
                style = SELECTED_CATEGORY_STYLE,
                clickLog = null,
            ),
        ),
        fallbackRadioBar(
            paramKey = "filterOpenStatuses",
            offText = "영업 중",
            onText = "영업 중",
            onValue = "OPEN",
        ),
        fallbackRadioBar(
            paramKey = "filterConditions",
            offText = "최근 활동",
            onText = "최근 활동",
            onValue = "RECENT_ACTIVITY",
        ),
        HomeFilterBar.RadioBar(
            type = HomeFilterBarType.RADIO_BAR,
            paramKey = "sortType",
            options = listOf(
                HomeFilterRadioOption(
                    chip = fallbackChip(text = "거리순", selected = true),
                    paramValue = HomeSortType.DISTANCE_ASC.name,
                    clickLog = null,
                ),
                HomeFilterRadioOption(
                    chip = fallbackChip(text = "최신순", selected = true),
                    paramValue = HomeSortType.LATEST.name,
                    clickLog = null,
                ),
            ),
        ),
        fallbackRadioBar(
            paramKey = "targetStores",
            offText = "사장님 직영점만",
            onText = "사장님 직영점만",
            onValue = HomeStoreType.BOSS_STORE.name,
        ),
    )

    private fun fallbackRadioBar(
        paramKey: String,
        offText: String,
        onText: String,
        onValue: String,
    ): HomeFilterBar.RadioBar = HomeFilterBar.RadioBar(
        type = HomeFilterBarType.RADIO_BAR,
        paramKey = paramKey,
        options = listOf(
            HomeFilterRadioOption(
                chip = fallbackChip(text = offText),
                paramValue = null,
                clickLog = null,
            ),
            HomeFilterRadioOption(
                chip = fallbackChip(text = onText, selected = true),
                paramValue = onValue,
                clickLog = null,
            ),
        ),
    )

    private fun fallbackChip(
        text: String,
        selected: Boolean = false,
    ): SDChipModel = SDChipModel(
        image = null,
        text = SDTextModel(
            text = text,
            isHtml = false,
            fontColor = if (selected) "#FF858F" else "#5A5A5A",
        ),
        additionalText = null,
        style = if (selected) SELECTED_CATEGORY_STYLE else DEFAULT_CHIP_STYLE,
    )

    private fun makeSelectedCategoryChip(category: StoreCategoryItem, fontColor: String): SDChipModel = SDChipModel(
        image = SDImageModel(url = category.imageUrl, style = SDImageStyleModel(width = 16.0, height = 16.0)),
        text = SDTextModel(text = category.name, isHtml = false, fontColor = fontColor),
        additionalText = null,
        style = null,
    )

    private fun sendClickFilterLog(
        paramKey: String,
        option: HomeFilterRadioOption,
        previousState: HomeUIState,
    ) {
        option.clickLog?.let {
            sendClickEvent(it)
            return
        }

        sendLegacyFallbackFilterLog(
            paramKey = paramKey,
            paramValue = option.paramValue,
            previousState = previousState,
        )
    }

    private fun sendLegacyFallbackFilterLog(
        paramKey: String,
        paramValue: String?,
        previousState: HomeUIState,
    ) {
        when (paramKey) {
            "filterConditions" -> {
                val contains = previousState.filterConditionsType.contains(FilterConditionsTypeModel.RECENT_ACTIVITY)
                LogManager.sendEvent(
                    ClickEvent(
                        screen = screenName,
                        objectType = LogObjectType.BUTTON,
                        objectId = LogObjectId.RECENT_ACTIVITY_FILTER,
                        additionalParams = mapOf(ParameterName.VALUE to contains.toString())
                    )
                )
            }
            "sortType" -> sendClickSorting(paramValue ?: HomeSortType.DISTANCE_ASC.name)
            "targetStores" -> sendClickBossFilter(paramValue == HomeStoreType.BOSS_STORE.name)
        }
    }

    private fun sendActionBarClickLog(link: SDLinkModel) {
        val actionBar = allBars()
            .filterIsInstance<HomeFilterBar.ActionBar>()
            .firstOrNull { it.button.link == link }
        val log = actionBar?.clickLog ?: return
        sendClickEvent(log)
    }

    private fun sendCurrentCategoryCloseLog() {
        val current = allBars()
            .filterIsInstance<HomeFilterBar.CategoryBar>()
            .firstOrNull()
            ?.currentCategoryFilter
        val log = current?.clickLog ?: return
        sendClickEvent(log)
    }

    private fun sendClickEvent(log: SDClickLogModel) {
        SDClickLogger.send(log)
    }

    fun sendClickStore(store: StoreModel) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.STORE,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to store.storeId,
                    ParameterName.STORE_TYPE to store.storeType
                )
            )
        )
    }

    fun sendClickAdvertisementCardLog(ad: AdvertisementModelV2) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.ADVERTISEMENT,
                additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to ad.advertisementId.toString())
            )
        )
    }

    fun sendClickCurrentLocationLog() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.CURRENT_LOCATION
            )
        )
    }

    fun sendClickAddress() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.ADDRESS
            )
        )
    }

    fun sendClickCategoryFilter() {
        val log = allBars()
            .filterIsInstance<HomeFilterBar.CategoryBar>()
            .firstOrNull()
            ?.categoriesFilterClickLog
        if (log != null) {
            sendClickEvent(log)
            return
        }
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.CATEGORY_FILTER
            )
        )
    }

    fun sendClickBossFilter(value: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.BOSS_FILTER,
                additionalParams = mapOf(ParameterName.VALUE to value.toString())
            )
        )
    }

    fun sendClickSorting(sortType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SORTING,
                additionalParams = mapOf(ParameterName.VALUE to sortType)
            )
        )
    }

    fun sendClickVisitButtonLog() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.VISIT
            )
        )
    }

    fun sendClickMarkerLog(store: StoreModel) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.MARKER,
                objectId = LogObjectId.STORE,
                additionalParams = mapOf(ParameterName.STORE_ID to store.storeId)
            )
        )
    }

    fun sendClickAdvertisementMarkerLog(advertisementId: Int) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.MARKER,
                objectId = LogObjectId.ADVERTISEMENT,
                additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to advertisementId.toString())
            )
        )
    }

    fun sendClickStoreInList(storeId: String, storeType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.HOME_LIST,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.STORE,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId,
                    ParameterName.STORE_TYPE to storeType
                )
            )
        )
    }

    fun sendClickCategoryFilterInList() {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.HOME_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.CATEGORY_FILTER
            )
        )
    }

    fun sendClickSortingInList(value: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.HOME_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SORTING,
                additionalParams = mapOf(ParameterName.VALUE to value)
            )
        )
    }

    fun sendClickAdvertisementInList(advertisementId: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.HOME_LIST,
                objectType = LogObjectType.BANNER,
                objectId = LogObjectId.ADVERTISEMENT,
                additionalParams = mapOf(ParameterName.ADVERTISEMENT_ID to advertisementId)
            )
        )
    }

    fun sendClickBossFilterInList(value: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.HOME_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.BOSS_FILTER,
                additionalParams = mapOf(ParameterName.VALUE to value.toString())
            )
        )
    }

    fun sendClickOnlyVisitInList(value: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.HOME_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.ONLY_VISIT,
                additionalParams = mapOf(ParameterName.VALUE to value.toString())
            )
        )
    }

    fun sendClickRecentActivityFilterInList(value: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.HOME_LIST,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.RECENT_ACTIVITY_FILTER,
                additionalParams = mapOf(ParameterName.VALUE to value.toString())
            )
        )
    }

    companion object {
        private const val KEY_MAP_POSITION = "map_position"
        private val DEFAULT_CHIP_STYLE = SDSurfaceStyleModel(
            backgroundColor = "#FFFFFF",
            border = SDBorderModel(color = "#D0D0D0", width = 1.0),
        )
        private val SELECTED_CATEGORY_STYLE = SDSurfaceStyleModel(
            backgroundColor = "#FFF3F4",
            border = SDBorderModel(color = "#FF858F", width = 1.0),
        )
    }
}
