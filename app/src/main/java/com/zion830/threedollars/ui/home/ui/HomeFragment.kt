package com.zion830.threedollars.ui.home.ui

import android.Manifest
import android.content.Intent
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.ext.toServerDrivenPlainText
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.USER_STORE
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2Empty
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.ui.dialog.DirectionBottomDialog
import com.zion830.threedollars.ui.dialog.MarketingDialog
import com.zion830.threedollars.ui.dialog.StorePhotoDialog
import com.zion830.threedollars.ui.dialog.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreMapViewRecyclerAdapter
import com.zion830.threedollars.ui.home.data.storePreviewStoreIdOrNull
import com.zion830.threedollars.ui.home.data.storePreviewStoreTypeOrNull
import com.zion830.threedollars.ui.home.ui.compose.HomeBottomSheetContent
import com.zion830.threedollars.ui.home.ui.compose.HomeFilterChipsRow
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.home.viewModel.SearchAddressViewModel
import com.zion830.threedollars.ui.map.ui.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.map.ui.FullScreenMapActivity
import com.zion830.threedollars.ui.edit.ui.EditStoreFragment
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossReviewWriteActivity
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossReviewDetailActivity
import com.zion830.threedollars.ui.storeDetail.contributor.ui.StoreContributorActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationArgs
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationCategoryArgs
import com.zion830.threedollars.ui.storeDetail.user.ui.MoreImageActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreReviewDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2Event
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2Activity
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2PlatformAction
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2LinkRoute
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2UiState
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2ViewModel
import com.zion830.threedollars.ui.storeDetail.v2.storeDetailV2Route
import com.zion830.threedollars.ui.storeDetail.v2.canSubmitStoreDetailReviewReport
import com.zion830.threedollars.ui.storeDetail.v2.imageIndexFor
import com.zion830.threedollars.ui.write.ui.AddStoreDetailFragment
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.ShareFormat
import com.zion830.threedollars.utils.SizeUtils
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.goToPermissionSetting
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.shareWithKakao
import com.zion830.threedollars.utils.showToast
import com.zion830.threedollars.utils.subscribeToTopicFirebase
import dagger.hilt.android.AndroidEntryPoint
import base.compose.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()
    private val legacyStoreDetailViewModel: StoreDetailViewModel by activityViewModels()

    private val storeDetailV2ViewModel: StoreDetailV2ViewModel by viewModels()

    private lateinit var adapter: AroundStoreMapViewRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    private var hasRequestedLocationPermission = false
    private var locationPermissionDialog: AlertDialog? = null

    private var isFirstLoad = true

    private var homeBottomSheetFullListTopPx by mutableIntStateOf(0)
    private var isStoreDetailExpanded by mutableStateOf(false)

    private val homeBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (isStoreDetailExpanded) {
                isStoreDetailExpanded = false
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
        parentFragmentManager.setFragmentResultListener(EditStoreFragment.STORE_EDITED_RESULT_KEY, viewLifecycleOwner) { _, _ ->
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
            startActivity(
                StoreDetailV2Activity.getIntent(
                    context = requireContext(),
                    storeId = storeId.toLong(),
                    storeType = USER_STORE,
                )
            )
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
                val intent = StoreDetailV2Activity.getIntent(
                    context = requireContext(),
                    storeId = item.storeModel.storeId.toLongOrNull(),
                    storeType = item.storeModel.storeType,
                )
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
            val intent = StoreDetailV2Activity.getIntent(
                context = requireContext(),
                storeId = item.storeModel.storeId.toLongOrNull(),
                storeType = item.storeModel.storeType,
                startCertification = true,
            )
            startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
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
                    contentPadding = PaddingValues(horizontal = 0.dp),
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
                val storeDetailState = storeDetailV2ViewModel.uiState.collectAsStateWithLifecycle().value
                val storeDetailFavoriteOverride = storeDetailV2ViewModel.favoriteOverride.collectAsStateWithLifecycle().value
                HomeBottomSheetContent(
                    homeListSection = homeListSection,
                    storeScreen = storeScreen,
                    storeDetailScreen = (storeDetailState as? StoreDetailV2UiState.Content)?.screen,
                    isStoreDetailLoading = storeDetailState is StoreDetailV2UiState.Loading,
                    selectedStoreExpanded = isStoreDetailExpanded,
                    onSelectedStoreExpandedChange = { isStoreDetailExpanded = it },
                    onStoreDetailAction = storeDetailV2ViewModel::onAction,
                    onStoreDetailFavoriteToggle = storeDetailV2ViewModel::toggleFavorite,
                    storeDetailFavoriteOverride = storeDetailFavoriteOverride,
                    onStoreDetailViewLog = storeDetailV2ViewModel::sendViewLog,
                    onStoreDetailImpression = storeDetailV2ViewModel::sendImpression,
                    onCardClick = ::moveHomeListCardDetail,
                    onLoadNextPage = viewModel::fetchNextHomeListSection,
                    onClosePreview = viewModel::closeStorePreview,
                    onActionClick = ::handleStorePreviewAction,
                    onFavoriteClick = ::toggleStorePreviewFavorite,
                    onAddPhotoClick = if (canAddPhotoToStorePreview()) ::moveStorePreviewPhotoAdd else null,
                    fullListTopPx = homeBottomSheetFullListTopPx,
                    onFullListBackgroundVisibleChange = { isVisible ->
                        binding.homeFullListTopBackgroundView.isVisible = isVisible
                    },
                    onVisibleHeightChange = ::updateLocationButtonBottomMargin,
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
                        if (screen == null) isStoreDetailExpanded = false
                    }
                }
                launch {
                    viewModel.selectedStorePreviewStoreId.collect { storeId ->
                        if (storeId == null) return@collect
                        isStoreDetailExpanded = false
                        val deviceLocation = naverMapFragment.currentPosition.value
                            ?.takeIf { isLocationAvailable() }
                        storeDetailV2ViewModel.load(
                            storeId = storeId,
                            deviceLatitude = deviceLocation?.latitude,
                            deviceLongitude = deviceLocation?.longitude,
                        )
                    }
                }
                launch {
                    storeDetailV2ViewModel.events.collect(::handleStoreDetailV2Event)
                }
                launch {
                    combine(
                        storeDetailV2ViewModel.uiState,
                        storeDetailV2ViewModel.favoriteOverride,
                    ) { state, favoriteOverride ->
                        favoriteOverride ?: (state as? StoreDetailV2UiState.Content)
                            ?.screen
                            ?.sections
                            ?.filterIsInstance<com.threedollar.common.serverdriven.model.StoreDetailSectionModel.Preview>()
                            ?.firstOrNull()
                            ?.additionalInfos
                            ?.isSubscriber
                    }.collect { favorite ->
                        favorite?.let(viewModel::updateSelectedStorePreviewFavorite)
                    }
                }
                launch {
                    legacyStoreDetailViewModel.photoDeleted.collect { success ->
                        if (success) storeDetailV2ViewModel.onChildResult(updated = true)
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

    private fun handleStorePreviewAction(actionBar: StoreActionBarModel) {
        viewModel.sendStorePreviewActionLog(actionBar)
        val button = actionBar.button
        val link = button.link
        if (link != null) {
            handleStorePreviewLink(link)
            return
        }
        button.customAction?.let(::handleStorePreviewCustomAction)
    }

    private fun handleStorePreviewLink(link: SDLinkModel) {
        val route = currentStorePreviewRoute()
        when (link.storeDetailV2Route()) {
            StoreDetailV2LinkRoute.Visit -> {
                val storeId = link.link.queryValue("storeId")?.toIntOrNull()
                    ?: route?.storeId?.toIntOrNullExact()
                    ?: return showUnsupportedStoreDetailAction()
                moveStorePreviewVisit(storeId)
            }
            StoreDetailV2LinkRoute.Contributors -> {
                val storeId = route?.storeId ?: return showUnsupportedStoreDetailAction()
                startActivityForResult(
                    StoreContributorActivity.getIntent(requireContext(), storeId.toString()),
                    Constants.SHOW_STORE_BY_CATEGORY,
                )
            }
            StoreDetailV2LinkRoute.Reviews -> {
                val storeId = route?.storeId?.toIntOrNullExact() ?: return showUnsupportedStoreDetailAction()
                val intent = if (route.storeType == BOSS_STORE) {
                    BossReviewDetailActivity.getIntent(requireContext(), storeId = storeId.toString())
                } else {
                    StoreReviewDetailActivity.getInstance(requireContext(), storeId)
                }
                startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
            }
            StoreDetailV2LinkRoute.Dynamic,
            StoreDetailV2LinkRoute.External -> handleFilterDeepLink(link)
            StoreDetailV2LinkRoute.Unsupported -> showUnsupportedStoreDetailAction()
        }
    }

    private fun handleStorePreviewCustomAction(customAction: SDCustomActionModel) {
        when (customAction.actionType) {
            "STORE_PREVIEW_SECTION_SHARE" -> shareStorePreview(customAction)
            "STORE_PREVIEW_SECTION_NAVIGATION" -> showStorePreviewDirection(customAction)
            "STORE_PREVIEW_SECTION_REVIEW_WRITE" -> moveStorePreviewReviewWrite(customAction)
            "STORE_PREVIEW_SECTION_CLOSE" -> viewModel.closeStorePreview()
        }
    }

    private fun handleStoreDetailV2Event(event: StoreDetailV2Event) {
        when (event) {
            is StoreDetailV2Event.ShowMessage -> showToast(
                event.message?.takeIf(String::isNotBlank) ?: getString(CommonR.string.connection_failed)
            )
            is StoreDetailV2Event.CloseContainer -> {
                event.message?.let(::showToast)
                isStoreDetailExpanded = false
                viewModel.closeStorePreview()
                viewModel.refreshHomeListSectionAfterStoreUpdate()
            }
            is StoreDetailV2Event.Platform -> handleStoreDetailV2PlatformAction(event.action)
            is StoreDetailV2Event.ShowReviewReportDialog -> showStoreReviewReportReasonDialog(event)
        }
    }

    private fun handleStoreDetailV2PlatformAction(action: StoreDetailV2PlatformAction) {
        when (action) {
            is StoreDetailV2PlatformAction.OpenLink -> handleStorePreviewLink(action.link)
            is StoreDetailV2PlatformAction.Share -> shareStorePreview(action.customAction)
            is StoreDetailV2PlatformAction.Navigation -> showStorePreviewDirection(action.customAction)
            is StoreDetailV2PlatformAction.ReviewWrite -> moveStorePreviewReviewWrite(action.customAction)
            is StoreDetailV2PlatformAction.EditStore -> openStoreEdit(action.customAction)
            is StoreDetailV2PlatformAction.ReportStore -> showStoreDeleteReasonDialog()
            is StoreDetailV2PlatformAction.AddImage -> moveStorePreviewPhotoAdd()
            is StoreDetailV2PlatformAction.EnlargeImage -> enlargeStoreImage(action.customAction)
            is StoreDetailV2PlatformAction.ReportReview -> showStoreReviewReportDialog(action.customAction)
            is StoreDetailV2PlatformAction.CopyAddress -> copyStoreDetailAddress(action.customAction)
            is StoreDetailV2PlatformAction.EnlargeMap -> enlargeStoreDetailMap(action.customAction)
        }
    }

    private fun openStoreEdit(customAction: SDCustomActionModel) {
        val storeId = customAction.extraParams.longValue("STORE_ID")
            ?: viewModel.selectedStorePreviewStoreId.value
            ?: return
        val resolvedStoreId = storeId.toIntOrNullExact() ?: return showUnsupportedStoreDetailAction()
        parentFragmentManager.addNewFragment(
            R.id.layout_container,
            EditStoreFragment.newInstance(resolvedStoreId),
            EditStoreFragment::class.java.name,
            false,
        )
    }

    private fun enlargeStoreImage(customAction: SDCustomActionModel) {
        val storeId = customAction.extraParams.longValue("STORE_ID")
            ?: viewModel.selectedStorePreviewStoreId.value
            ?: return
        val resolvedStoreId = storeId.toIntOrNullExact() ?: return showUnsupportedStoreDetailAction()
        val imageIndex = (storeDetailV2ViewModel.uiState.value as? StoreDetailV2UiState.Content)
            ?.screen
            ?.imageIndexFor(customAction)
            ?: 0
        StorePhotoDialog.getInstance(imageIndex, resolvedStoreId)
            .show(parentFragmentManager, StorePhotoDialog::class.java.name)
    }

    private fun copyStoreDetailAddress(customAction: SDCustomActionModel) {
        val address = customAction.extraParams.stringValue("ADDRESS")
            ?: customAction.extraParams.stringValue("VALUE")
            ?: return
        (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).text = address
        showToast(getString(CommonR.string.address_copied))
    }

    private fun enlargeStoreDetailMap(customAction: SDCustomActionModel) {
        val params = customAction.extraParams
        val currentLocation = currentHomeListCard()?.marker?.location
        startActivity(
            FullScreenMapActivity.getIntent(
                context = requireContext(),
                latitude = params.doubleValue("LATITUDE") ?: currentLocation?.latitude,
                longitude = params.doubleValue("LONGITUDE") ?: currentLocation?.longitude,
                name = params.stringValue("STORE_NAME")?.toServerDrivenPlainText() ?: currentStorePreviewTitle(),
            )
        )
    }

    private fun showStoreDeleteReasonDialog() {
        // 기존 DeleteStoreDialog를 V2 result mode로 열어 선택된 사유만 ViewModel에 전달한다.
        val resultKey = "store_detail_v2_delete_reason"
        parentFragmentManager.setFragmentResultListener(resultKey, viewLifecycleOwner) { _, bundle ->
            bundle.getString("delete_reason_type")?.let(storeDetailV2ViewModel::reportMissingStore)
        }
        com.zion830.threedollars.ui.dialog.DeleteStoreDialog.getInstance(resultKey)
            .show(parentFragmentManager, com.zion830.threedollars.ui.dialog.DeleteStoreDialog::class.java.name)
    }

    private fun showStoreReviewReportDialog(customAction: SDCustomActionModel) {
        storeDetailV2ViewModel.requestReviewReport(customAction)
    }

    private fun showStoreReviewReportReasonDialog(event: StoreDetailV2Event.ShowReviewReportDialog) {
        var selectedIndex = -1
        val detailInput = EditText(requireContext()).apply {
            hint = getString(CommonR.string.review_report_reason_detail_hint)
            isVisible = false
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(CommonR.string.review_report_dialog_title))
            .setSingleChoiceItems(event.reasons.map { it.description }.toTypedArray(), selectedIndex, null)
            .setView(detailInput)
            .setPositiveButton(CommonR.string.report_confirm, null)
            .setNegativeButton(CommonR.string.cancel, null)
            .create()
        dialog.setOnShowListener {
            val confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            fun updateValidation() {
                val reason = event.reasons.getOrNull(selectedIndex)
                detailInput.isVisible = reason?.hasReasonDetail == true
                confirmButton.isEnabled = canSubmitStoreDetailReviewReport(
                    reasons = event.reasons,
                    selectedIndex = selectedIndex,
                    detail = detailInput.text?.toString().orEmpty(),
                )
            }
            dialog.listView.setOnItemClickListener { _, _, position, _ ->
                selectedIndex = position
                if (event.reasons[position].hasReasonDetail.not()) detailInput.setText("")
                updateValidation()
            }
            detailInput.doAfterTextChanged { updateValidation() }
            confirmButton.setOnClickListener {
                val reason = event.reasons.getOrNull(selectedIndex) ?: return@setOnClickListener
                val detail = detailInput.text?.toString().orEmpty()
                if (!canSubmitStoreDetailReviewReport(event.reasons, selectedIndex, detail)) return@setOnClickListener
                storeDetailV2ViewModel.submitReviewReport(
                    customAction = event.customAction,
                    reason = reason.type,
                    reasonDetail = detail.takeIf { reason.hasReasonDetail },
                )
                dialog.dismiss()
            }
            updateValidation()
        }
        dialog.show()
    }

    private fun toggleStorePreviewFavorite(isSubscriber: Boolean) {
        val storeId = viewModel.selectedStorePreviewStoreId.value
            ?: currentStorePreviewRoute()?.storeId
            ?: return
        if (isSubscriber) {
            viewModel.deleteFavoriteFromStorePreview(storeId)
        } else {
            viewModel.putFavoriteFromStorePreview(storeId)
        }
    }

    private fun moveStorePreviewReviewWrite(customAction: SDCustomActionModel) {
        val route = currentStorePreviewRoute(
            fallbackStoreId = customAction.extraParams.longValue("STORE_ID"),
            fallbackStoreType = customAction.extraParams.stringValue("STORE_TYPE"),
        ) ?: return
        val intent = if (route.storeType == BOSS_STORE) {
            BossReviewWriteActivity.getIntent(requireContext(), route.storeId.toString())
        } else {
            StoreDetailV2Activity.getIntent(
                context = requireContext(),
                storeId = route.storeId,
                storeType = route.storeType,
                openReviewWrite = true,
            )
        }
        startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
    }

    private fun moveStorePreviewPhotoAdd() {
        val route = currentStorePreviewRoute() ?: return
        val storeId = route.storeId.toIntOrNullExact() ?: return showUnsupportedStoreDetailAction()
        startActivityForResult(
            MoreImageActivity.getIntent(requireContext(), storeId),
            Constants.SHOW_STORE_BY_CATEGORY,
        )
    }

    private fun canAddPhotoToStorePreview(): Boolean {
        return currentStorePreviewRoute()?.storeType == USER_STORE
    }

    private fun showUnsupportedStoreDetailAction() {
        showToast(getString(CommonR.string.store_detail_link_not_supported))
    }

    private fun moveHomeListCardDetail(card: HomeListCardModel.BasicCard) {
        viewModel.selectHomeListCard(card)
    }

    private fun shareStorePreview(customAction: SDCustomActionModel) {
        val params = customAction.extraParams
        val route = currentStorePreviewRoute(
            fallbackStoreId = params.longValue("STORE_ID"),
            fallbackStoreType = params.stringValue("STORE_TYPE"),
        )
        val storeId = route?.storeId?.toString()
        val storeType = route?.storeType ?: USER_STORE
        val storeName = params.stringValue("STORE_NAME")?.toServerDrivenPlainText() ?: currentStorePreviewTitle()
        val currentCard = currentHomeListCard()
        val latitude = params.doubleValue("LATITUDE") ?: currentCard?.marker?.location?.latitude
        val longitude = params.doubleValue("LONGITUDE") ?: currentCard?.marker?.location?.longitude
        if (storeId.isNullOrBlank() || latitude == null || longitude == null) {
            showToast(getString(CommonR.string.exist_location_error))
            return
        }
        val location = LatLng(latitude, longitude)
        val kakaoType = if (storeType == BOSS_STORE) {
            getString(CommonR.string.scheme_host_kakao_link_food_truck_type)
        } else {
            getString(CommonR.string.scheme_host_kakao_link_road_food_type)
        }
        requireContext().shareWithKakao(
            shareFormat = ShareFormat(
                url = getString(CommonR.string.kakao_map_format),
                storeName = storeName,
                location = location,
            ),
            title = if (storeType == BOSS_STORE) {
                getString(CommonR.string.share_kakao_food_truck_title, storeName)
            } else {
                getString(CommonR.string.share_kakao_road_food_title, storeName)
            },
            description = if (storeType == BOSS_STORE) {
                getString(CommonR.string.share_kakao_food_truck, storeName)
            } else {
                getString(CommonR.string.share_kakao_road_food, storeName)
            },
            imageUrl = "https://storage.threedollars.co.kr/share/share-with-kakao.png",
            storeId = storeId,
            type = kakaoType,
        )
    }

    private fun moveStorePreviewVisit(storeId: Int) {
        val args = currentStoreCertificationArgs(storeId) ?: run {
            showToast(getString(CommonR.string.exist_location_error))
            isStoreDetailExpanded = false
            viewModel.closeStorePreview()
            return
        }
        startActivityForResult(
            StoreCertificationActivity.getIntent(requireContext(), args),
            Constants.SHOW_STORE_BY_CATEGORY,
        )
    }

    private fun showStorePreviewDirection(customAction: SDCustomActionModel) {
        val params = customAction.extraParams
        DirectionBottomDialog.getInstance(
            latitude = params.doubleValue("LATITUDE"),
            longitude = params.doubleValue("LONGITUDE"),
            storeName = params.stringValue("STORE_NAME")?.toServerDrivenPlainText() ?: currentStorePreviewTitle(),
        ).show(parentFragmentManager, "")
    }

    private fun currentStorePreviewTitle(): String {
        return viewModel.selectedStoreScreen.value
            ?.sections
            ?.filterIsInstance<StoreSectionModel.Preview>()
            ?.firstOrNull()
            ?.header
            ?.title
            .displayText()
            .orEmpty()
    }

    private fun currentStoreCertificationArgs(storeId: Int): StoreCertificationArgs? {
        val card = currentHomeListCard()
        val markerLocation = card?.marker?.location ?: return null
        return StoreCertificationArgs(
            storeId = storeId,
            storeName = currentStorePreviewTitle().ifBlank { card.header.title.displayText() },
            latitude = markerLocation.latitude,
            longitude = markerLocation.longitude,
            categories = card.metadata.primary.mapNotNull { chip ->
                val name = chip.text.displayText().takeIf { it.isNotBlank() } ?: return@mapNotNull null
                StoreCertificationCategoryArgs(
                    name = name,
                    imageUrl = chip.image?.url.orEmpty(),
                )
            },
        )
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
            if (resultCode == android.app.Activity.RESULT_OK) {
                data?.favoriteStateOrNull()?.let(viewModel::updateSelectedStorePreviewFavorite)
                data?.takeIf { it.getBooleanExtra(StoreDetailActivity.EXTRA_IS_UPDATED, false) }?.let { result ->
                    val userStore = IntentCompat.getSerializableExtra(result, StoreDetailActivity.EXTRA_USER_STORE, UserStoreModel::class.java)
                    userStore?.let {
                        viewModel.updateStoreItem(it)
                        naverMapFragment.updateMarkerPosition(
                            it.storeId.toString(),
                            it.location.latitude,
                            it.location.longitude
                        )
                    }
                }
            }
            refreshHomeAfterStoreUpdate()
        }
    }

    private fun refreshHomeAfterStoreUpdate() {
        viewModel.refreshHomeListSectionAfterStoreUpdate()
        if (viewModel.selectedStoreScreen.value == null) return
        viewModel.refreshSelectedStorePreview()
        storeDetailV2ViewModel.onChildResult(updated = true)
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

private fun String.queryValue(key: String): String? = Uri.parse(this).getQueryParameter(key)

private fun Long.toIntOrNullExact(): Int? =
    takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }?.toInt()

private fun Intent.favoriteStateOrNull(): Boolean? {
    return if (hasExtra(StoreDetailActivity.EXTRA_IS_FAVORITE)) {
        getBooleanExtra(StoreDetailActivity.EXTRA_IS_FAVORITE, false)
    } else {
        null
    }
}

private fun Map<String, SDClickLogValue>.stringValue(key: String): String? {
    return when (val value = this[key]) {
        is SDClickLogValue.StringValue -> value.value
        is SDClickLogValue.IntValue -> value.value.toString()
        is SDClickLogValue.LongValue -> value.value.toString()
        is SDClickLogValue.DoubleValue -> value.value.toString()
        is SDClickLogValue.BoolValue -> value.value.toString()
        SDClickLogValue.Null, null -> null
    }
}

private fun Map<String, SDClickLogValue>.longValue(key: String): Long? {
    return when (val value = this[key]) {
        is SDClickLogValue.StringValue -> value.value.toLongOrNull()
        is SDClickLogValue.IntValue -> value.value.toLong()
        is SDClickLogValue.LongValue -> value.value
        is SDClickLogValue.DoubleValue -> value.value.toLong()
        is SDClickLogValue.BoolValue, SDClickLogValue.Null, null -> null
    }
}

private fun Map<String, SDClickLogValue>.doubleValue(key: String): Double? {
    return when (val value = this[key]) {
        is SDClickLogValue.StringValue -> value.value.toDoubleOrNull()
        is SDClickLogValue.IntValue -> value.value.toDouble()
        is SDClickLogValue.LongValue -> value.value.toDouble()
        is SDClickLogValue.DoubleValue -> value.value
        is SDClickLogValue.BoolValue, SDClickLogValue.Null, null -> null
    }
}
