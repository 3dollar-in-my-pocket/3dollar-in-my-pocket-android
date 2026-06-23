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
import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterCurrentCategory
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
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
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.StoreModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.threedollar.domain.home.data.user.UserModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.screen.repository.ScreenRepository
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem
import com.zion830.threedollars.ui.home.data.HomeAroundStoreRequestParamsBuilder
import com.zion830.threedollars.ui.home.data.ChipAction
import com.zion830.threedollars.ui.home.data.HomeFilterCellType
import com.zion830.threedollars.ui.home.data.HomeListSectionQueryParamsBuilder
import com.zion830.threedollars.ui.home.data.HomeUIState
import com.zion830.threedollars.ui.home.data.storePreviewStoreIdOrNull
import com.zion830.threedollars.ui.home.data.toFallbackStorePreviewScreen
import com.zion830.threedollars.ui.home.data.withStorePreviewFavoriteOverride
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

    private val storePreviewFavoriteOverrides = mutableMapOf<Long, Boolean>()

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
                        val selectedCardId = if (preserveSelectedStore) {
                            cards.selectedCardIdAfterRefresh(
                                previousSelectedCardId = previousSelectedCardId,
                                previousSelectedStoreId = previousSelectedStoreId,
                            )
                        } else {
                            cards.firstOrNull()?.cardId
                        }
                        _selectedHomeListCardId.value = selectedCardId
                        if (preserveSelectedStore && previousSelectedStoreId != null && _selectedStoreScreen.value != null) {
                            cards.firstOrNull { it.cardId == selectedCardId }
                                ?.let { updateStorePreviewFromCard(previousSelectedStoreId, it) }
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
        val card = fallbackCard ?: _homeListSection.value.cards
            .filterIsInstance<HomeListCardModel.BasicCard>()
            .firstOrNull { it.storePreviewStoreIdOrNull() == storeId }
        card?.let { updateStorePreviewFromCard(storeId, it) }
    }

    private fun updateStorePreviewFromCard(
        storeId: Long,
        card: HomeListCardModel.BasicCard,
    ) {
        card.toFallbackStorePreviewScreen(
            isSubscriber = storePreviewFavoriteOverrides[storeId] ?: false,
        )?.let { fallbackScreen ->
            _selectedStoreScreen.value = fallbackScreen
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
                    updateStorePreviewFavorite(storeId = targetStoreId, isFavorite = true)
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
                    updateStorePreviewFavorite(storeId = targetStoreId, isFavorite = false)
                    _storePreviewToast.emit("가게 저장을 취소했어요")
                } else {
                    _serverError.emit(response.message)
                }
            }
        }
    }

    fun updateSelectedStorePreviewFavorite(isFavorite: Boolean) {
        updateStorePreviewFavorite(storeId = _selectedStorePreviewStoreId.value, isFavorite = isFavorite)
    }

    private fun updateStorePreviewFavorite(storeId: Long?, isFavorite: Boolean) {
        storeId?.let {
            storePreviewFavoriteOverrides[it] = isFavorite
        }
        _selectedStoreScreen.update { screen ->
            screen?.withStorePreviewFavoriteOverride(isFavorite)
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
        filterCertifiedStores: Boolean? = null,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uiState.update {
                it.copy(
                    filterCertifiedStores = filterCertifiedStores ?: it.filterCertifiedStores,
                )
            }
            fetchAroundStores()
            updateFilterCells()
        }
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

        _uiState.update { state ->
            val newSelection = state.radioSelection.toMutableMap()
            newSelection[paramKey] = optionIndex
            state.copy(radioSelection = newSelection)
        }
        option.clickLog?.let { sendClickEvent(it) }
            ?: sendLegacyFallbackFilterLog(paramKey = paramKey, paramValue = option.paramValue)
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
            }
        }
        _uiState.update { it.copy(radioSelection = newSelection) }
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

    private fun sendLegacyFallbackFilterLog(
        paramKey: String,
        paramValue: String?,
    ) {
        when (paramKey) {
            "filterConditions" -> {
                LogManager.sendEvent(
                    ClickEvent(
                        screen = screenName,
                        objectType = LogObjectType.BUTTON,
                        objectId = LogObjectId.RECENT_ACTIVITY_FILTER,
                        additionalParams = mapOf(ParameterName.VALUE to (paramValue != null).toString())
                    )
                )
            }
            "sortType" -> paramValue?.let(::sendClickSorting)
            "targetStores" -> sendClickBossFilter(paramValue != null)
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
