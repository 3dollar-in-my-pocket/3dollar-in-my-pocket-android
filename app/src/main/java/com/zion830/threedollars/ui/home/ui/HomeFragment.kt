package com.zion830.threedollars.ui.home.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessaging
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2Empty
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.home.presentation.data.HomeSortType
import com.home.presentation.data.HomeStoreType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.listener.OnSnapPositionChangeListener
import com.threedollar.common.listener.SnapOnScrollListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.CLICK_ADDRESS_FIELD
import com.threedollar.common.utils.Constants.CLICK_AD_CARD
import com.threedollar.common.utils.Constants.CLICK_BOSS_FILTER
import com.threedollar.common.utils.Constants.CLICK_CATEGORY_FILTER
import com.threedollar.common.utils.Constants.CLICK_MARKER
import com.threedollar.common.utils.Constants.CLICK_RECENT_ACTIVITY_FILTER
import com.threedollar.common.utils.Constants.CLICK_SORTING
import com.threedollar.common.utils.Constants.CLICK_STORE
import com.threedollar.common.utils.Constants.CLICK_VISIT
import com.threedollar.common.utils.SharedPrefUtils
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.ui.dialog.MarketingDialog
import com.zion830.threedollars.ui.dialog.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreMapViewRecyclerAdapter
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.home.viewModel.SearchAddressViewModel
import com.zion830.threedollars.ui.map.ui.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.NaverMapUtils.calculateDistance
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.goToPermissionSetting
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.showToast
import com.zion830.threedollars.utils.subscribeToTopicFirebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: AroundStoreMapViewRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    private var homeStoreType = HomeStoreType.ALL
    private var homeSortType = HomeSortType.DISTANCE_ASC
    private var filterConditionsType: List<FilterConditionsTypeModel> = listOf()
    
    private var hasRequestedLocationPermission = false
    private var locationPermissionDialog: AlertDialog? = null
    
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
        initViewModel()
        initFlow()
        initButton()
        initScroll()

        binding.filterConditionsSpeechBubbleLayout.isVisible = !sharedPrefUtils.getIsClickFilterConditions()

        viewModel.addressText.observe(viewLifecycleOwner) {
            binding.tvAddress.text = it ?: getString(CommonR.string.location_no_address)
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "HomeFragment", screenName = "home")
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
                            /*
                            광고가 index를 하나 차지하고 있어서 focusedIndex는 position에 -1을 해준다.
                            하지만 adapter.getItemMarker에서 markerModel을 가져올 때는 position 기준으로 가져와야 된다.
                            그러므로 가게 이동전 포커싱 돼 있던 아이콘을 비활성화 하기 위해 adapter.focusedIndex에 +1를 해준 값을 넣어주고
                            이동후 포커싱 되는 부분에서는 position을 통해 markerModel을 가져온다.
                             */
                            naverMapFragment.updateMarkerIcon(
                                drawableRes = R.drawable.ic_store_off,
                                position = adapter.focusedIndex,
                                markerModel = adapter.getItemMarker(if (adapter.focusedIndex <= 0) adapter.focusedIndex else adapter.focusedIndex + 1),
                                isSelected = false
                            )
                            adapter.focusedIndex = if (position > 0) position - 1 else position
                            naverMapFragment.updateMarkerIcon(
                                drawableRes = R.drawable.ic_mappin_focused_on,
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
        val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
        val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
        viewModel.requestHomeItem(
            naverMapFragment.getMapCenterLatLng(),
            if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
        )
        viewModel.getAdvertisement(latLng = naverMapFragment.getMapCenterLatLng())
    }

    private fun initAdapter() {
        adapter = AroundStoreMapViewRecyclerAdapter(object : OnItemClickListener<ContentModel> {
            override fun onClick(item: ContentModel) {
                val bundle = Bundle().apply {
                    putString("screen", "home")
                    putString("store_id", item.storeModel.storeId)
                    putString("type", item.storeModel.storeType)
                }
                EventTracker.logEvent(CLICK_STORE, bundle)
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
                val bundle = Bundle().apply {
                    putString("screen", "home")
                    putString("advertisement_id", item.advertisementId.toString())
                }
                EventTracker.logEvent(CLICK_AD_CARD, bundle)
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
            val bundle = Bundle().apply {
                putString("screen", "home")
                putString("store_id", item.storeModel.storeId)
            }
            EventTracker.logEvent(CLICK_VISIT, bundle)
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
                checkLocationPermissionForButton()
            }
        )
        childFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()
        
        // Check and request location permission after login
        lifecycleScope.launch {
            delay(500L)
            checkAndRequestLocationPermission()
        }
        
        naverMapFragment.currentPosition.observe(viewLifecycleOwner) {
            viewModel.updateCurrentLocation(it)
        }
    }

    private fun initButton() {
        binding.layoutAddress.onSingleClick {
            val bundle = Bundle().apply {
                putString("screen", "home")
            }
            EventTracker.logEvent(CLICK_ADDRESS_FIELD, bundle)
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                SearchAddressFragment.newInstance(),
                SearchAddressFragment::class.java.name
            )
        }

        binding.allMenuTextView.onSingleClick {
            val bundle = Bundle().apply {
                putString("screen", "home")
            }
            EventTracker.logEvent(CLICK_CATEGORY_FILTER, bundle)
            showSelectCategoryDialog()
        }

        binding.filterConditionsTextView.onSingleClick {
            sharedPrefUtils.setIsClickFilterConditions()
            binding.filterConditionsSpeechBubbleLayout.isVisible = !sharedPrefUtils.getIsClickFilterConditions()
            filterConditionsType = if (filterConditionsType.isEmpty()) {
                listOf(FilterConditionsTypeModel.RECENT_ACTIVITY)
            } else {
                listOf()
            }
            val bundle = Bundle().apply {
                putString("screen", "home")
                putBoolean("value", filterConditionsType.contains(FilterConditionsTypeModel.RECENT_ACTIVITY))
            }
            EventTracker.logEvent(CLICK_RECENT_ACTIVITY_FILTER, bundle)

            viewModel.updateHomeFilterEvent(filterConditionsType = filterConditionsType)
        }
        binding.filterTextView.onSingleClick {
            homeSortType = if (homeSortType == HomeSortType.DISTANCE_ASC) {
                HomeSortType.LATEST
            } else {
                HomeSortType.DISTANCE_ASC
            }
            val bundle = Bundle().apply {
                putString("screen", "home")
                putString("type", homeSortType.name)
            }
            EventTracker.logEvent(CLICK_SORTING, bundle)
            viewModel.updateHomeFilterEvent(homeSortType = homeSortType)
        }

        binding.bossFilterTextView.onSingleClick {
            homeStoreType = if (homeStoreType == HomeStoreType.ALL) HomeStoreType.BOSS_STORE else HomeStoreType.ALL
            val bundle = Bundle().apply {
                putString("screen", "home")
                putString("value", if (homeStoreType == HomeStoreType.BOSS_STORE) "on" else "off")
            }
            EventTracker.logEvent(CLICK_BOSS_FILTER, bundle)
            viewModel.updateHomeFilterEvent(homeStoreType = homeStoreType)
        }

        binding.listViewTextView.onSingleClick {
            findNavController().navigate(R.id.action_home_to_home_list_view)
        }
        binding.tvRetrySearch.onSingleClick {
            val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
            val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
            viewModel.requestHomeItem(
                naverMapFragment.getMapCenterLatLng(),
                if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
            )
            viewModel.getAdvertisement(latLng = naverMapFragment.getMapCenterLatLng())
            binding.tvRetrySearch.isVisible = false
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.selectCategory.collect {
                        val text = if (it.categoryId.isEmpty()) getString(CommonR.string.fragment_home_all_menu) else it.name
                        val textColor = if (it.categoryId.isEmpty()) R.color.gray70 else R.color.pink
                        val background =
                            if (it.categoryId.isEmpty()) DesignSystemR.drawable.rect_white_radius10_stroke_gray30 else DesignSystemR.drawable.rect_white_radius10_stroke_black_fill_black

                        binding.run {
                            allMenuTextView.text = text
                            allMenuTextView.setTextColor(resources.getColor(textColor, null))
                            allMenuTextView.setBackgroundResource(background)
                            if (it.imageUrl.isEmpty()) {
                                allMenuTextView.setCompoundDrawablesWithIntrinsicBounds(
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_category), null, null, null
                                )
                            } else {
                                loadImageUriIntoDrawable(it.imageUrl.toUri()) { drawable ->
                                    allMenuTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                                }
                            }
                        }
                        val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
                        val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
                        viewModel.requestHomeItem(
                            naverMapFragment.getMapCenterLatLng(),
                            if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
                        )
                    }
                }
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
                    viewModel.aroundStoreModels.collect { adAndStoreItems ->
                        if (adAndStoreItems.isEmpty()) return@collect
                        val resultList = mutableListOf<AdAndStoreItem>()
                        resultList.addAll(adAndStoreItems)
                        resultList.add(1, viewModel.advertisementModel.value ?: AdvertisementModelV2Empty())
                        adapter.submitList(resultList)
                        val list = adAndStoreItems.filterIsInstance<ContentModel>()
                        naverMapFragment.addStoreMarkers(R.drawable.ic_store_off, list) {
                            onStoreClicked(it)
                        }
                        naverMapFragment.updateMarkerIcon(
                            drawableRes = R.drawable.ic_mappin_focused_on,
                            position = 0,
                            markerModel = list.firstOrNull()?.markerModel,
                            isSelected = true
                        )
                        delay(200L)
                        binding.aroundStoreRecyclerView.scrollToPosition(0)
                    }
                }
                launch {
                    viewModel.homeFilterEvent.collect {
                        val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
                        val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
                        viewModel.requestHomeItem(
                            naverMapFragment.getMapCenterLatLng(),
                            if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
                        )

                        binding.run {
                            if (it.filterConditionsType.contains(FilterConditionsTypeModel.RECENT_ACTIVITY)) {
                                filterConditionsTextView.setTextColor(resources.getColor(R.color.pink, null))
                                filterConditionsTextView.setBackgroundResource(DesignSystemR.drawable.rect_radius10_pink100_stroke_pink)
                            } else {
                                filterConditionsTextView.setTextColor(resources.getColor(R.color.gray40, null))
                                filterConditionsTextView.setBackgroundResource(DesignSystemR.drawable.rect_white_radius10_stroke_gray30)
                            }
                            if (it.homeStoreType == HomeStoreType.BOSS_STORE) {
                                bossFilterTextView.setTextColor(resources.getColor(R.color.pink, null))
                                bossFilterTextView.setBackgroundResource(DesignSystemR.drawable.rect_radius10_pink100_stroke_pink)
                            } else {
                                bossFilterTextView.setTextColor(resources.getColor(R.color.gray40, null))
                                bossFilterTextView.setBackgroundResource(DesignSystemR.drawable.rect_white_radius10_stroke_gray30)
                            }
                            filterTextView.text = if (it.homeSortType == HomeSortType.DISTANCE_ASC) {
                                getString(CommonR.string.fragment_home_filter_distance)
                            } else {
                                getString(CommonR.string.fragment_home_filter_latest)
                            }
                        }
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
                    searchViewModel.searchResultLocation.collect {
                        naverMapFragment.moveCamera(it)
                        binding.tvAddress.text =
                            getCurrentLocationName(it) ?: getString(CommonR.string.location_no_address)
                    }
                }
            }
        }
    }

    private fun loadImageUriIntoDrawable(imageUri: Uri, callback: (Drawable?) -> Unit) {
        Glide.with(requireContext())
            .load(imageUri)
            .override(64)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    callback(null)
                }
            })
    }

    private fun showSelectCategoryDialog() {
        val dialog = SelectCategoryDialogFragment()
        dialog.show(parentFragmentManager, "")
    }

    private fun onStoreClicked(adAndStoreItem: AdAndStoreItem) {
        val position = adapter.getItemPosition(adAndStoreItem)
        if (position >= 0) {
            val bundle = Bundle().apply {
                putString("screen", "home")
                if (adAndStoreItem is ContentModel) {
                    putString("store_id", adAndStoreItem.storeModel.storeId)
                    putString("type", adAndStoreItem.storeModel.storeType)
                } else if (adAndStoreItem is BossNearStoreResponse.BossNearStoreModel) {
                    putString("store_id", adAndStoreItem.bossStoreId)
                    putString("type", "BOSS_STORE")
                }
            }
            EventTracker.logEvent(CLICK_MARKER, bundle)
            naverMapFragment.updateMarkerIcon(
                drawableRes = R.drawable.ic_store_off,
                position = adapter.focusedIndex,
                markerModel = adapter.getItemMarker(if (adapter.focusedIndex <= 0) adapter.focusedIndex else adapter.focusedIndex + 1),
                isSelected = false
            )
            adapter.focusedIndex = if (position > 0) position - 1 else position
            naverMapFragment.updateMarkerIcon(
                drawableRes = R.drawable.ic_mappin_focused_on,
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
                // 권한 있음: 현재 위치로
                naverMapFragment.moveToCurrentLocation(false)
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
            // 권한 있음: 현재 위치로
            naverMapFragment.moveToCurrentLocation(true)
        } else {
            // 권한 없음: 항상 설명 다이얼로그 표시
            showLocationPermissionDialog()
        }
    }
    
    private fun useDefaultLocation() {
        naverMapFragment.moveCamera(NaverMapUtils.DEFAULT_LOCATION)
        
        val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
        val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
        viewModel.requestHomeItem(
            NaverMapUtils.DEFAULT_LOCATION,
            if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
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
        naverMapFragment.moveToCurrentLocation(true)
        
        lifecycleScope.launch {
            delay(1000L)
            val currentLocation = naverMapFragment.currentPosition.value
            if (currentLocation != null) {
                val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
                val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
                viewModel.requestHomeItem(
                    currentLocation,
                    if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
                )
                viewModel.getAdvertisement(latLng = currentLocation)
            }
        }
    }
    

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GET_LOCATION_PERMISSION) {
            naverMapFragment.onActivityResult(requestCode, resultCode, data)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Check if permission was granted from settings
        if (hasRequestedLocationPermission && isLocationAvailable()) {
            onLocationPermissionGranted()
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)
    
    override fun onDestroy() {
        super.onDestroy()
        locationPermissionDialog?.dismiss()
        locationPermissionDialog = null
    }
}