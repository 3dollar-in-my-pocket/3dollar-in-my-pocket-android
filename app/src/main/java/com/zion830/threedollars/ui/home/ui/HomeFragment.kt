package com.zion830.threedollars.ui.home.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
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
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.BOSS_STORE
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
import com.zion830.threedollars.ui.dialog.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreMapViewRecyclerAdapter
import com.zion830.threedollars.ui.home.ui.compose.HomeBottomSheetContent
import com.zion830.threedollars.ui.home.ui.compose.HomeFilterChipsRow
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.home.viewModel.SearchAddressViewModel
import com.zion830.threedollars.ui.map.ui.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
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
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: AroundStoreMapViewRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    private var hasRequestedLocationPermission = false
    private var locationPermissionDialog: AlertDialog? = null

    private var isFirstLoad = true

    private var homeBottomSheetFullListTopPx by mutableIntStateOf(0)

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> {
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

    override fun initView() {
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
            startActivity(StoreDetailActivity.getIntent(requireContext(), storeId = storeId))
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
                if (item.storeModel.storeType == BOSS_STORE) {
                    val intent =
                        BossStoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId)
                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
                } else {
                    val intent =
                        StoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId.toInt(), false)
                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
                }

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
            val intent = StoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId.toInt(), true)
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
                HomeBottomSheetContent(
                    homeListSection = homeListSection,
                    storeScreen = storeScreen,
                    onCardClick = viewModel::selectHomeListCard,
                    onLoadNextPage = viewModel::fetchNextHomeListSection,
                    onClosePreview = viewModel::closeStorePreview,
                    onActionClick = ::handleStorePreviewAction,
                    fullListTopPx = homeBottomSheetFullListTopPx,
                    onFullListBackgroundVisibleChange = { isVisible ->
                        binding.homeFullListTopBackgroundView.isVisible = isVisible
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
                        if (cards.isEmpty()) return@collect
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
        val url = link.link
        if (url.isBlank()) return
        if (link.type == "APP_SCHEME" && url.startsWith("/visit")) {
            val storeId = url.queryValue("storeId")?.toIntOrNull() ?: return
            startActivityForResult(
                StoreDetailActivity.getIntent(requireContext(), storeId = storeId, startCertification = true),
                Constants.SHOW_STORE_BY_CATEGORY,
            )
            return
        }
        handleFilterDeepLink(link)
    }

    private fun handleStorePreviewCustomAction(customAction: SDCustomActionModel) {
        when (customAction.actionType) {
            "STORE_PREVIEW_SECTION_FAVORITE" -> {
                customAction.extraParams.longValue("STORE_ID")?.let { storeId ->
                    viewModel.putFavoriteFromStorePreview(storeId.toString())
                }
            }
            "STORE_PREVIEW_SECTION_UNFAVORITE" -> {
                customAction.extraParams.longValue("STORE_ID")?.let { storeId ->
                    viewModel.deleteFavoriteFromStorePreview(storeId.toString())
                }
            }
            "STORE_PREVIEW_SECTION_SHARE" -> shareStorePreview(customAction)
            "STORE_PREVIEW_SECTION_NAVIGATION" -> showStorePreviewDirection(customAction)
        }
    }

    private fun shareStorePreview(customAction: SDCustomActionModel) {
        val params = customAction.extraParams
        val storeId = params.longValue("STORE_ID")?.toString()
        val storeType = params.stringValue("STORE_TYPE")
        val storeName = params.stringValue("STORE_NAME") ?: currentStorePreviewTitle()
        val latitude = params.doubleValue("LATITUDE")
        val longitude = params.doubleValue("LONGITUDE")
        val location = if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
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
            title = storeName,
            description = storeName,
            imageUrl = "https://storage.threedollars.co.kr/share/share-with-kakao.png",
            storeId = storeId,
            type = kakaoType,
        )
    }

    private fun showStorePreviewDirection(customAction: SDCustomActionModel) {
        val params = customAction.extraParams
        DirectionBottomDialog.getInstance(
            latitude = params.doubleValue("LATITUDE"),
            longitude = params.doubleValue("LONGITUDE"),
            storeName = params.stringValue("STORE_NAME") ?: currentStorePreviewTitle(),
        ).show(parentFragmentManager, "")
    }

    private fun currentStorePreviewTitle(): String {
        return viewModel.selectedStoreScreen.value
            ?.sections
            ?.filterIsInstance<com.threedollar.common.serverdriven.model.StoreSectionModel.Preview>()
            ?.firstOrNull()
            ?.header
            ?.title
            ?.text
            .orEmpty()
    }

    private fun currentHomeListCards(): List<HomeListCardModel.BasicCard> {
        return viewModel.homeListSection.value.cards.filterIsInstance<HomeListCardModel.BasicCard>()
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
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            hasRequestedLocationPermission -> {
                // "다시 묻지 않음" 상태: 설명 다이얼로그
                showLocationPermissionDialog()
            }
            else -> {
                // 첫 요청: 바로 권한 요청
                hasRequestedLocationPermission = true
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
    
    private fun useDefaultLocation() {
        naverMapFragment.moveCamera(NaverMapUtils.DEFAULT_LOCATION)

        viewModel.fetchAroundStores(
            mapPosition = NaverMapUtils.DEFAULT_LOCATION,
            userLocation = NaverMapUtils.DEFAULT_LOCATION,
        )
        viewModel.getAdvertisement(latLng = NaverMapUtils.DEFAULT_LOCATION)
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
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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

        if (requestCode == Constants.SHOW_STORE_BY_CATEGORY && resultCode == android.app.Activity.RESULT_OK) {
            val isUpdated = data?.getBooleanExtra(StoreDetailActivity.EXTRA_IS_UPDATED, false) ?: false
            if (isUpdated && data != null) {
                val userStore = IntentCompat.getSerializableExtra(data, StoreDetailActivity.EXTRA_USER_STORE, UserStoreModel::class.java)
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

private fun Map<String, SDClickLogValue>.stringValue(key: String): String? {
    return when (val value = this[key]) {
        is SDClickLogValue.StringValue -> value.value
        is SDClickLogValue.IntValue -> value.value.toString()
        is SDClickLogValue.DoubleValue -> value.value.toString()
        is SDClickLogValue.BoolValue -> value.value.toString()
        SDClickLogValue.Null, null -> null
    }
}

private fun Map<String, SDClickLogValue>.longValue(key: String): Long? {
    return when (val value = this[key]) {
        is SDClickLogValue.StringValue -> value.value.toLongOrNull()
        is SDClickLogValue.IntValue -> value.value.toLong()
        is SDClickLogValue.DoubleValue -> value.value.toLong()
        is SDClickLogValue.BoolValue, SDClickLogValue.Null, null -> null
    }
}

private fun Map<String, SDClickLogValue>.doubleValue(key: String): Double? {
    return when (val value = this[key]) {
        is SDClickLogValue.StringValue -> value.value.toDoubleOrNull()
        is SDClickLogValue.IntValue -> value.value.toDouble()
        is SDClickLogValue.DoubleValue -> value.value
        is SDClickLogValue.BoolValue, SDClickLogValue.Null, null -> null
    }
}
