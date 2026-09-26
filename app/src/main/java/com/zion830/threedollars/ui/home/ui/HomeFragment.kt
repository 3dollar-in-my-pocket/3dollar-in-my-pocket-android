package com.zion830.threedollars.ui.home.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.firebase.messaging.FirebaseMessaging
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.CustomEvent
import com.threedollar.common.analytics.EventName
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.listener.OnSnapPositionChangeListener
import com.threedollar.common.listener.SnapOnScrollListener
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDLocationBoundsModel
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.USER_STORE
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2Empty
import com.threedollar.domain.home.data.store.ContentModel
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.ui.dialog.MarketingDialog
import com.zion830.threedollars.ui.dialog.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreMapViewRecyclerAdapter
import com.zion830.threedollars.ui.home.data.storePreviewStoreIdOrNull
import com.zion830.threedollars.ui.home.data.storePreviewStoreTypeOrNull
import com.zion830.threedollars.ui.home.ui.compose.HomeBottomSheetContent
import com.zion830.threedollars.ui.home.ui.compose.StorePreviewSduiContent
import com.zion830.threedollars.ui.home.ui.compose.HomeFilterChipsRow
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.home.viewModel.SearchAddressViewModel
import com.zion830.threedollars.ui.map.ui.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.MoreImageActivity
import com.zion830.threedollars.ui.write.ui.AddStoreDetailFragment
import com.zion830.threedollars.ui.edit.ui.EditStoreFragment
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiIntent
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailDestination
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiActivity
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiNavigator
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiEffects
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiNavigationBarRoute
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiRoute
import com.zion830.threedollars.ui.storeDetail.sdui.viewModel.StoreDetailSduiViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.SizeUtils
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.goToPermissionSetting
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.showToast
import com.zion830.threedollars.utils.subscribeToTopicFirebase
import dagger.hilt.android.AndroidEntryPoint
import base.compose.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private const val FOCUS_BOUNDS_PADDING_DP = 24f

/** 주소 바와 같은 좌우 여백. 필터 줄은 화면 끝까지 스크롤되고 첫·끝 칩만 이만큼 들어간다. */
private const val HOME_FILTER_HORIZONTAL_PADDING_DP = 20

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private val storeDetailViewModel: StoreDetailSduiViewModel by viewModels()

    private val storeDetailResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        storeDetailViewModel.dispatch(StoreDetailSduiUiIntent.OnStoreChanged)
        refreshHomeAfterStoreUpdate()
    }

    private val storeDetailNavigator by lazy(LazyThreadSafetyMode.NONE) {
        StoreDetailSduiNavigator(
            activity = requireActivity(),
            fragmentContainerId = R.id.layout_container,
            dispatch = storeDetailViewModel::dispatch,
            launchForResult = storeDetailResultLauncher::launch,
            onClose = {
                viewModel.closeStorePreview()
                refreshHomeAfterStoreUpdate()
            },
        )
    }

    private lateinit var adapter: AroundStoreMapViewRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    private var hasRequestedLocationPermission = false
    private var locationPermissionDialog: AlertDialog? = null

    private var isFirstLoad = true

    private var homeBottomSheetFullListTopPx by mutableIntStateOf(0)

    private val homeBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (viewModel.isStoreDetailExpanded.value) {
                viewModel.setStoreDetailExpanded(false)
            } else {
                viewModel.closeStorePreview()
            }
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        when {
            results[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                onLocationPermissionGranted()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // 재요청 가능 상태: 다이얼로그 없이 기본 위치 사용
                useDefaultLocation()
            }
            else -> {
                // "다시 묻지 않음" 상태: 설명 다이얼로그 표시
                showLocationPermissionDialog()
            }
        }
    }

    // Android 13+ 알림 권한을 따로 요청하면 위치 권한 다이얼로그와 겹쳐 한쪽이 자동 거부될 수 있어 함께 요청한다.
    private val startUpPermissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun initView() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, homeBackPressedCallback)
        requireActivity().supportFragmentManager.setFragmentResultListener(
            EditStoreFragment.STORE_EDITED_RESULT_KEY,
            viewLifecycleOwner,
        ) { _, _ ->
            storeDetailViewModel.dispatch(StoreDetailSduiUiIntent.OnStoreChanged)
            refreshHomeAfterStoreUpdate()
        }
        initMap()
        initAdapter()
        initFilterComposeView()
        initHomeBottomSheetBehavior()
        initHomeBottomSheetComposeView()
        initViewModel()
        initFlow()
        initButton()
        initScroll()

        arguments?.getInt(AddStoreDetailFragment.NAVIGATE_STORE_ID, 0)?.takeIf { it != 0 }?.let { storeId ->
            arguments?.remove(AddStoreDetailFragment.NAVIGATE_STORE_ID)
            startActivity(StoreDetailSduiActivity.getIntent(requireContext(), storeId = storeId.toString()))
        }
        consumeStorePreviewDeepLink()
    }

    private fun initScroll() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.aroundStoreRecyclerView)
        binding.aroundStoreRecyclerView.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (adapter.getItemLocation(position) != null) {
                            naverMapFragment.updateMarkerIcon(
                                drawableRes = DesignSystemR.drawable.ic_store_off,
                                position = adapter.focusedIndex,
                                markerModel = adapter.getItemMarker(if (adapter.focusedIndex <= 0) adapter.focusedIndex else adapter.focusedIndex + 1),
                                isSelected = false
                            )
                            adapter.focusedIndex = if (position > 0) position - 1 else position
                            naverMapFragment.updateMarkerIcon(
                                drawableRes = DesignSystemR.drawable.ic_mappin_focused_on,
                                position = adapter.focusedIndex,
                                markerModel = adapter.getItemMarker(position),
                                isSelected = true
                            )
                            adapter.getItemLocation(position)?.let {
                                naverMapFragment.moveCameraWithAnim(it)
                            }
                        }
                    }
                })
        )
    }

    private fun initViewModel() {
        viewModel.getUserInfo()
    }

    private fun initAdapter() {
        adapter = AroundStoreMapViewRecyclerAdapter(object : OnItemClickListener<ContentModel> {
            override fun onClick(item: ContentModel) {
                viewModel.sendClickStore(item.storeModel)
                val intent = StoreDetailSduiActivity.getIntent(requireContext(), item.storeModel.storeId)
                startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)

            }
        }, object : OnItemClickListener<AdvertisementModelV2> {
            override fun onClick(item: AdvertisementModelV2) {
                viewModel.sendClickAdvertisementCardLog(item)
                if (item.link.type == "APP_SCHEME") {
                    startActivity(
                        Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                            putExtra("link", item.link.url)
                        },
                    )
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link.url)))
                }
            }
        }) { item ->
            viewModel.sendClickVisitButtonLog()
            startActivity(StoreCertificationActivity.getIntent(requireContext(), item.storeModel.storeId.toInt()))
        }
        binding.aroundStoreRecyclerView.adapter = adapter
    }

    private fun initMap() {
        naverMapFragment = NearStoreNaverMapFragment(
            cameraMoved = {
                binding.tvRetrySearch.isVisible = true
            },
            onLocationButtonClicked = {
                viewModel.sendClickCurrentLocationLog()
                checkLocationPermissionForButton()
            }
        )
        naverMapFragment.onAdMarkerClicked = { advertisementId ->
            viewModel.sendClickAdvertisementMarkerLog(advertisementId)
        }
        childFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()
        
        // Check and request location permission after login
        lifecycleScope.launch {
            delay(500L)
            checkAndRequestLocationPermission()
        }
        
        naverMapFragment.currentPosition.observe(viewLifecycleOwner) {
            viewModel.updateCurrentLocation(it)
        }

        naverMapFragment.mapPosition.observe(viewLifecycleOwner) {
            viewModel.updateMapPosition(it)
        }

        naverMapFragment.mapViewPortDistance.observe(viewLifecycleOwner) {
            viewModel.updateDistanceM(it)
        }
    }

    private fun initFilterComposeView() {
        binding.filterComposeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        binding.filterComposeView.setContent {
            AppTheme {
                val cells = viewModel.filterCells.collectAsStateWithLifecycle().value
                HomeFilterChipsRow(
                    cells = cells,
                    contentPadding = PaddingValues(horizontal = HOME_FILTER_HORIZONTAL_PADDING_DP.dp),
                    onCategoryClick = {
                        viewModel.sendClickCategoryFilter()
                        showSelectCategoryDialog()
                    },
                    onRadioClick = viewModel::selectRadioOption,
                    onActionClick = viewModel::handleActionLink,
                    onCloseSelectedCategoryClick = viewModel::closeSelectedCategory,
                )
            }
        }
    }

    private fun initHomeBottomSheetComposeView() {
        binding.homeBottomSheetComposeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        binding.homeBottomSheetComposeView.setContent {
            AppTheme {
                val homeListSection = viewModel.homeListSection.collectAsStateWithLifecycle().value
                val storeScreen = viewModel.selectedStoreScreen.collectAsStateWithLifecycle().value
                val isStoreDetailExpanded = viewModel.isStoreDetailExpanded.collectAsStateWithLifecycle().value
                val selectedStoreId = viewModel.selectedStorePreviewStoreId.collectAsStateWithLifecycle().value
                val storeDetailListState = remember(selectedStoreId) { LazyListState() }
                LaunchedEffect(isStoreDetailExpanded) {
                    if (!isStoreDetailExpanded) storeDetailListState.scrollToItem(0)
                }
                StoreDetailSduiEffects(
                    viewModel = storeDetailViewModel,
                    navigator = storeDetailNavigator,
                    listState = storeDetailListState,
                )
                val storePreview = storeDetailViewModel.preview.collectAsStateWithLifecycle().value
                    ?.takeIf { it.additionalInfos?.storeId == null || it.additionalInfos?.storeId == selectedStoreId?.toString() }
                HomeBottomSheetContent(
                    homeListSection = homeListSection,
                    storeScreen = storeScreen,
                    onCardClick = ::selectHomeListCard,
                    onLoadNextPage = viewModel::fetchNextHomeListSection,
                    onStorePreviewClick = { viewModel.setStoreDetailExpanded(true) },
                    fullListTopPx = homeBottomSheetFullListTopPx,
                    onFullListBackgroundVisibleChange = { isVisible ->
                        binding.homeFullListTopBackgroundView.isVisible = isVisible
                    },
                    onVisibleHeightChange = ::updateLocationButtonBottomMargin,
                    onMapViewClick = viewModel::sendClickMapViewLog,
                    storeDetailExpanded = isStoreDetailExpanded,
                    onStoreDetailExpandedChange = viewModel::setStoreDetailExpanded,
                    storeDetailContent = { isDisplayed, placeholderHeader ->
                        StoreDetailSduiRoute(
                            viewModel = storeDetailViewModel,
                            navigator = storeDetailNavigator,
                            onBack = { viewModel.setStoreDetailExpanded(false) },
                            onClose = viewModel::closeStorePreview,
                            listState = storeDetailListState,
                            inSheet = true,
                            collectEffects = false,
                            isDisplayed = isDisplayed,
                            placeholderHeader = placeholderHeader,
                        )
                    },
                    storePreview = storePreview?.let { preview ->
                        { showHeaderButtons ->
                            val isFavorite = storeDetailViewModel.state.collectAsStateWithLifecycle().value.isFavorite
                            StorePreviewSduiContent(
                                preview = preview,
                                isFavorite = isFavorite,
                                onFavoriteClick = { storeDetailViewModel.dispatch(StoreDetailSduiUiIntent.OnFavoriteClick) },
                                onClose = viewModel::closeStorePreview,
                                onAction = { storeDetailViewModel.dispatch(StoreDetailSduiUiIntent.OnAction(it)) },
                                onImageClick = ::showStorePreviewImages,
                                onAddPhotoClick = if (canAddPhotoToStorePreview()) ::moveStorePreviewPhotoAdd else null,
                                showHeaderButtons = showHeaderButtons,
                            )
                        }
                    },
                    storeDetailNavigationBar = { navigationModifier ->
                        StoreDetailSduiNavigationBarRoute(
                            viewModel = storeDetailViewModel,
                            listState = storeDetailListState,
                            onBack = { viewModel.setStoreDetailExpanded(false) },
                            onClose = viewModel::closeStorePreview,
                            modifier = navigationModifier,
                        )
                    },
                )
            }
        }
    }

    private fun initHomeBottomSheetBehavior() {
        binding.homeBottomSheetComposeView.apply {
            isVisible = true
            elevation = SizeUtils.dpToPx(10f).toFloat()
            bringToFront()
            translationY = 0f
        }
        binding.homeFullListTopBackgroundView.isVisible = false
        binding.root.doOnLayout { updateHomeBottomSheetFullListTop() }
        binding.filterComposeView.doOnLayout { updateHomeBottomSheetFullListTop() }
    }

    private fun updateHomeBottomSheetFullListTop() {
        homeBottomSheetFullListTopPx = binding.filterComposeView.bottom
            .takeIf { it > 0 }
            ?: SizeUtils.dpToPx(188f)
    }

    private fun updateLocationButtonBottomMargin(sheetVisibleHeightPx: Int) {
        val bottomMarginPx = sheetVisibleHeightPx + SizeUtils.dpToPx(HomeSheetLayout.LOCATION_BUTTON_GAP_FROM_SHEET_DP)
        naverMapFragment.updateLocationButtonBottomMargin(bottomMarginPx)
    }

    private fun initButton() {
        binding.layoutAddress.onSingleClick {
            viewModel.sendClickAddress()
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                SearchAddressFragment.newInstance(),
                SearchAddressFragment::class.java.name
            )
        }

        binding.listViewTextView.onSingleClick {
            findNavController().navigate(R.id.action_home_to_home_list_view)
        }
        binding.tvRetrySearch.onSingleClick {
            viewModel.fetchAroundStores()
            viewModel.getAdvertisement(latLng = naverMapFragment.getMapCenterLatLng())
            binding.tvRetrySearch.isVisible = false
        }
    }

    private fun initFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userInfo.collect {
                        if (it.marketingConsent == "UNVERIFIED") {
                            showMarketingDialog()
                        }
                        if (!LegacySharedPrefUtils.getFirstMarketing()) {
                            if (it.marketingConsent == "APPROVE") subscribeToTopicFirebase(true)
                        }
                    }
                }
                launch {
                    viewModel.carouselUpdate
                        .collect { itemList ->
                            collectCarouselItemList(itemList, viewModel.consumeShouldResetScroll())
                        }
                }
                launch {
                    viewModel.filterDeepLink.collect { link ->
                        handleFilterDeepLink(link)
                    }
                }
                launch {
                    viewModel.homeListSection.collect { section ->
                        val cards = section.cards.filterIsInstance<HomeListCardModel.BasicCard>()
                        if (cards.isEmpty()) {
                            naverMapFragment.clearMarker()
                            return@collect
                        }
                        naverMapFragment.addHomeListMarkers(
                            drawableRes = DesignSystemR.drawable.ic_store_off,
                            list = cards,
                        ) { card ->
                            viewModel.selectHomeListMarker(card)
                        }
                        updateHomeListMarkerSelection(cards, viewModel.selectedHomeListCardId.value)
                    }
                }
                launch {
                    viewModel.selectedHomeListCardId.collect { selectedCardId ->
                        updateHomeListMarkerSelection(currentHomeListCards(), selectedCardId)
                    }
                }
                launch {
                    viewModel.selectedStoreScreen.collect { screen ->
                        homeBackPressedCallback.isEnabled = screen != null
                    }
                }
                launch {
                    viewModel.selectedStorePreviewStoreId.filterNotNull().collect { storeId ->
                        val location = viewModel.uiState.value.userLocation
                        storeDetailViewModel.dispatch(
                            StoreDetailSduiUiIntent.Load(
                                storeId = storeId.toString(),
                                latitude = location.latitude,
                                longitude = location.longitude,
                                withPreview = true,
                            )
                        )
                    }
                }
                launch {
                    viewModel.isStoreDetailExpanded.filter { it }.collect {
                        storeDetailViewModel.dispatch(StoreDetailSduiUiIntent.OnDisplayed)
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
                launch {
                    viewModel.storePreviewToast.collect {
                        showToast(it)
                    }
                }
                launch {
                    viewModel.initialMapZoomLevel.collect { zoomLevel ->
                        zoomLevel?.let { naverMapFragment.applyInitialZoomLevel(it) }
                    }
                }
                launch {
                    viewModel.focusBounds.collect { bounds ->
                        moveMapToFocusBounds(bounds)
                    }
                }
                launch {
                    searchViewModel.searchResultLocation.collect {
                        naverMapFragment.moveCamera(it)
                        binding.tvAddress.text =
                            getCurrentLocationName(it) ?: getString(CommonR.string.location_no_address)
                    }
                }
                launch {
                    viewModel.currentLocation.collect {
                        binding.tvAddress.text = getCurrentLocationName(it) ?: getString(CommonR.string.location_no_address)
                    }
                }
            }
        }
    }

    private fun moveMapToFocusBounds(bounds: SDLocationBoundsModel) {
        val basePaddingPx = SizeUtils.dpToPx(FOCUS_BOUNDS_PADDING_DP)
        naverMapFragment.moveCameraToBounds(
            southWest = LatLng(bounds.southWest.latitude, bounds.southWest.longitude),
            northEast = LatLng(bounds.northEast.latitude, bounds.northEast.longitude),
            paddingPx = intArrayOf(
                basePaddingPx,
                binding.filterComposeView.bottom + basePaddingPx,
                basePaddingPx,
                SizeUtils.dpToPx(HomeSheetLayout.COLLAPSED_PEEK_HEIGHT_DP) + basePaddingPx,
            ),
        )
    }

    private fun handleFilterDeepLink(link: SDLinkModel) {
        val url = link.link
        if (url.isBlank()) return
        if (link.type == "APP_SCHEME") {
            startActivity(
                Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                    putExtra("link", url)
                }
            )
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun consumeStorePreviewDeepLink() {
        val intent = requireActivity().intent
        val storeId = intent.getLongExtra(DynamicLinkActivity.STORE_PREVIEW, 0L).takeIf { it > 0L } ?: return
        intent.removeExtra(DynamicLinkActivity.STORE_PREVIEW)
        viewModel.fetchStoreScreen(storeId)
    }

    private fun moveStorePreviewPhotoAdd() {
        val route = currentStorePreviewRoute() ?: return
        startActivityForResult(
            MoreImageActivity.getIntent(requireContext(), route.storeId.toInt()),
            Constants.SHOW_STORE_BY_CATEGORY,
        )
    }

    private fun canAddPhotoToStorePreview(): Boolean {
        return currentStorePreviewRoute()?.storeType == USER_STORE
    }

    private fun showStorePreviewImages(imageUrls: List<String>, index: Int) {
        if (imageUrls.isEmpty()) return
        storeDetailNavigator.navigate(StoreDetailDestination.ShowImages(imageUrls, index.coerceIn(0, imageUrls.lastIndex)))
    }

    /**
     * 홈 리스트 카드 탭: 지도를 그 가게로 옮기고 마커를 선택한 뒤 미리보기 시트를 띄운다(iOS 동일).
     * 가게 id 를 알 수 없는 카드만 링크로 바로 상세를 연다.
     */
    private fun selectHomeListCard(card: HomeListCardModel.BasicCard) {
        if (card.storePreviewStoreIdOrNull() == null) {
            moveHomeListCardDetail(card)
            return
        }
        viewModel.selectHomeListCard(card)
        naverMapFragment.moveCameraWithAnim(LatLng(card.marker.location.latitude, card.marker.location.longitude))
    }

    private fun moveHomeListCardDetail(card: HomeListCardModel.BasicCard) {
        viewModel.sendClickHomeListCard(card)
        val route = HomeStorePreviewRoute.fromLink(
            link = card.link?.link,
            fallbackStoreId = card.storePreviewStoreIdOrNull(),
            fallbackStoreType = card.storePreviewStoreTypeOrNull(),
        ) ?: return
        val intent = StoreDetailSduiActivity.getIntent(requireContext(), route.storeId.toString())
        startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
    }


    private fun currentHomeListCards(): List<HomeListCardModel.BasicCard> {
        return viewModel.homeListSection.value.cards.filterIsInstance<HomeListCardModel.BasicCard>()
    }

    private fun currentHomeListCard(): HomeListCardModel.BasicCard? {
        val selectedCardId = viewModel.selectedHomeListCardId.value
        return currentHomeListCards().firstOrNull { it.cardId == selectedCardId }
    }

    private fun currentStorePreviewRoute(
        fallbackStoreId: Long? = null,
        fallbackStoreType: String? = null,
    ): HomeStorePreviewRoute? {
        val card = currentHomeListCard()
        return HomeStorePreviewRoute.fromLink(
            link = card?.link?.link,
            fallbackStoreId = fallbackStoreId
                ?: viewModel.selectedStorePreviewStoreId.value
                ?: card?.storePreviewStoreIdOrNull(),
            fallbackStoreType = fallbackStoreType ?: card?.storePreviewStoreTypeOrNull(),
        )
    }

    private fun updateHomeListMarkerSelection(
        cards: List<HomeListCardModel.BasicCard>,
        selectedCardId: String?,
    ) {
        cards.forEachIndexed { index, card ->
            val selected = card.cardId == selectedCardId
            naverMapFragment.updateHomeListMarkerIcon(
                drawableRes = if (selected) DesignSystemR.drawable.ic_mappin_focused_on else DesignSystemR.drawable.ic_store_off,
                position = index,
                card = card,
                isSelected = selected,
            )
        }
    }

    private fun showSelectCategoryDialog() {
        SelectCategoryDialogFragment
            .newInstance(
                latLng = viewModel.uiState.value.userLocation
            )
            .show(parentFragmentManager, SelectCategoryDialogFragment.TAG)
    }

    private fun onStoreClicked(adAndStoreItem: AdAndStoreItem) {
        val position = adapter.getItemPosition(adAndStoreItem)
        if (position >= 0) {
            if (adAndStoreItem is ContentModel) {
                viewModel.sendClickMarkerLog(adAndStoreItem.storeModel)
            }

            naverMapFragment.updateMarkerIcon(
                drawableRes = DesignSystemR.drawable.ic_store_off,
                position = adapter.focusedIndex,
                markerModel = adapter.getItemMarker(if (adapter.focusedIndex <= 0) adapter.focusedIndex else adapter.focusedIndex + 1),
                isSelected = false
            )
            adapter.focusedIndex = if (position > 0) position - 1 else position
            naverMapFragment.updateMarkerIcon(
                drawableRes = DesignSystemR.drawable.ic_mappin_focused_on,
                position = adapter.focusedIndex,
                markerModel = adapter.getItemMarker(position),
                isSelected = true
            )
            naverMapFragment.moveCameraWithAnim(
                if (adAndStoreItem is ContentModel) {
                    LatLng(adAndStoreItem.storeModel.locationModel.latitude, adAndStoreItem.storeModel.locationModel.longitude)
                } else {
                    val location =
                        (adAndStoreItem as BossNearStoreResponse.BossNearStoreModel).location
                    LatLng(location.latitude, location.longitude)
                }
            )

            adapter.notifyDataSetChanged()
            binding.aroundStoreRecyclerView.scrollToPosition(position)
        }
    }

    private fun showMarketingDialog() {
        val dialog = MarketingDialog()
        dialog.setDialogListener(object : MarketingDialog.DialogListener {
            override fun accept(isMarketing: Boolean) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.putPushInformation(pushToken = it.result, isMarketing = isMarketing)
                    }
                }
            }
        })
        dialog.show(parentFragmentManager, dialog.tag)
    }
    
    private fun checkAndRequestLocationPermission() {
        when {
            isLocationAvailable() -> {
                loadHomeWithCurrentLocation(showAnim = false)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // 이전에 거부했지만 재요청 가능: 바로 권한 요청
                locationPermissionLauncher.launch(startUpPermissions)
            }
            hasRequestedLocationPermission -> {
                // "다시 묻지 않음" 상태: 설명 다이얼로그
                showLocationPermissionDialog()
            }
            else -> {
                // 첫 요청: 바로 권한 요청
                hasRequestedLocationPermission = true
                locationPermissionLauncher.launch(startUpPermissions)
            }
        }
    }
    
    private fun checkLocationPermissionForButton() {
        if (isLocationAvailable()) {
            naverMapFragment.enableLocationTracking()
            val mapCenter = naverMapFragment.getMapCenterLatLng()
            naverMapFragment.moveToCurrentLocation(true) { currentLocation ->
                val distance = NaverMapUtils.calculateDistance(mapCenter, currentLocation)
                if (distance > 100f) {
                    binding.tvRetrySearch.isVisible = true
                }
            }
        } else {
            showLocationPermissionDialog()
        }
    }
    
    /**
     * 현재 위치를 확인하지 못했을 때 사용할 위치로 지도를 이동한다.
     *
     * 마지막으로 확인된 위치가 있으면 그 위치를, 없으면 기본 위치(서울 중심)를 쓴다.
     */
    private fun useDefaultLocation() {
        val fallbackLocation = naverMapFragment.getCachedUserLocation() ?: NaverMapUtils.DEFAULT_LOCATION
        naverMapFragment.moveCamera(fallbackLocation)

        viewModel.fetchAroundStores(
            mapPosition = fallbackLocation,
            userLocation = fallbackLocation,
        )
        viewModel.getAdvertisement(latLng = fallbackLocation)
    }
    
    private fun showLocationPermissionDialog() {
        locationPermissionDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(CommonR.string.location_permission_title))
            .setMessage(getString(CommonR.string.location_permission_message))
            .setPositiveButton(
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getString(CommonR.string.location_permission_grant)
                } else {
                    getString(CommonR.string.location_permission_settings)
                }
            ) { dialog, _ ->
                dialog.dismiss()
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // 재요청 가능한 상태: 권한 요청
                    locationPermissionLauncher.launch(startUpPermissions)
                } else {
                    // "다시 묻지 않음" 상태: 설정 페이지로
                    requireContext().goToPermissionSetting()
                }
            }
            .setNegativeButton(getString(CommonR.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                useDefaultLocation()
            }
            .setCancelable(false)
            .create()
        
        locationPermissionDialog?.show()
    }
    
    private fun onLocationPermissionGranted() {
        loadHomeWithCurrentLocation(showAnim = true)
    }

    private fun loadHomeWithCurrentLocation(showAnim: Boolean) {
        naverMapFragment.enableLocationTracking()
        naverMapFragment.moveToCurrentLocation(showAnim) { currentLocation ->
            if (currentLocation == null) {
                useDefaultLocation()
                return@moveToCurrentLocation
            }

            viewModel.fetchAroundStores(
                mapPosition = currentLocation,
                userLocation = currentLocation,
            )
            viewModel.getAdvertisement(latLng = currentLocation)
        }
    }

    private suspend fun collectCarouselItemList(itemList: List<AdAndStoreItem>, shouldResetScroll: Boolean) {
        if (itemList.isEmpty()) return
        val resultList = mutableListOf<AdAndStoreItem>()
        resultList.addAll(itemList)
        resultList.add(
            1,
            viewModel.advertisementModel.value ?: AdvertisementModelV2Empty()
        )

        adapter.submitList(resultList)

        if (shouldResetScroll) {
            val list = itemList.filterIsInstance<ContentModel>()
            naverMapFragment.addStoreMarkers(DesignSystemR.drawable.ic_store_off, list) {
                onStoreClicked(it)
            }
            naverMapFragment.updateMarkerIcon(
                drawableRes = DesignSystemR.drawable.ic_mappin_focused_on,
                position = 0,
                markerModel = list.firstOrNull()?.markerModel,
                isSelected = true
            )
            delay(200L)
            binding.aroundStoreRecyclerView.scrollToPosition(0)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GET_LOCATION_PERMISSION) {
            naverMapFragment.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == Constants.SHOW_STORE_BY_CATEGORY) {
            refreshHomeAfterStoreUpdate()
        }
    }

    private fun refreshHomeAfterStoreUpdate() {
        viewModel.refreshHomeListSectionAfterStoreUpdate()
        if (viewModel.selectedStoreScreen.value == null) return
        viewModel.refreshSelectedStorePreview()
    }
    
    override fun onResume() {
        super.onResume()
        consumeStorePreviewDeepLink()
        // Check if permission was granted from settings
        if (hasRequestedLocationPermission && isLocationAvailable()) {
            onLocationPermissionGranted()
        }

        if (!isFirstLoad) {
            LogManager.sendEvent(CustomEvent(
                viewModel.screenName,
                EventName.HOME_REOPEN
            ))
        }
        isFirstLoad = false
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)
    
    override fun onDestroy() {
        super.onDestroy()
        locationPermissionDialog?.dismiss()
        locationPermissionDialog = null
    }
}
