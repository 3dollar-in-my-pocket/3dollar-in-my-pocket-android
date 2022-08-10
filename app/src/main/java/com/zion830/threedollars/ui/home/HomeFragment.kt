package com.zion830.threedollars.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.addstore.view.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.home.adapter.BossCategoriesRecyclerAdapter
import com.zion830.threedollars.ui.home.adapter.NearStoreRecyclerAdapter
import com.zion830.threedollars.ui.home.adapter.RoadFoodCategoriesRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.showToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnItemClickListener
import zion830.com.common.listener.OnSnapPositionChangeListener
import zion830.com.common.listener.SnapOnScrollListener


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: NearStoreRecyclerAdapter

    private lateinit var bossCategoriesAdapter: BossCategoriesRecyclerAdapter

    private lateinit var roadFoodCategoriesAdapter: RoadFoodCategoriesRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    private var isRoadFoodMode = true

    private var selectRoadFood = "All"
    private var selectFoodTruck = "All"

    fun getMapCenterLatLng() = try {
        naverMapFragment.getMapCenterLatLng()
    } catch (e: Exception) {
        null
    }

    override fun initView() {
        viewModel.getRoadFoodCategory()
        naverMapFragment = NearStoreNaverMapFragment {
            binding.tvRetrySearch.isVisible = true
        }
        childFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        viewModel.addressText.observe(viewLifecycleOwner) {
            binding.tvAddress.text = it ?: getString(R.string.location_no_address)
        }
        searchViewModel.searchResultLocation.observe(viewLifecycleOwner) {
            naverMapFragment.moveCamera(it)
            binding.tvAddress.text =
                getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }
        binding.modeChangeTextView.setOnClickListener {
            isRoadFoodMode = !isRoadFoodMode
            binding.modeChangeTextView.run {

                if (isRoadFoodMode) {
                    viewModel.getRoadFoodCategory()
                    naverMapFragment.onActivityResult(
                        Constants.MODE_ROAD_FOOD,
                        Activity.RESULT_OK,
                        null
                    )
                } else {
                    viewModel.getBossCategory()
                    naverMapFragment.onActivityResult(
                        Constants.MODE_FOOD_TRUCK,
                        Activity.RESULT_OK,
                        null
                    )
                }
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (isRoadFoodMode) R.drawable.ic_sync_pink else R.drawable.ic_sync_green,
                    0,
                    0,
                    0
                )
                text =
                    if (isRoadFoodMode) getString(R.string.road_food) else getString(R.string.food_truck)
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isRoadFoodMode) R.color.color_FFA1AA else R.color.color_green
                    )
                )
            }
            getNearStore()
            binding.bossCategoryRecyclerView.isVisible = !isRoadFoodMode
            binding.roadFoodCategoryRecyclerView.isVisible = isRoadFoodMode
        }

        binding.layoutAddress.setOnClickListener {
            EventTracker.logEvent(Constants.SEARCH_BTN_CLICKED)
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                SearchAddressFragment.newInstance(isRoadFoodMode),
                SearchAddressFragment::class.java.name
            )
        }

        adapter = NearStoreRecyclerAdapter(object : OnItemClickListener<StoreInfo?> {
            override fun onClick(item: StoreInfo?) {
                if (item != null) {
                    EventTracker.logEvent(Constants.STORE_CARD_BTN_CLICKED)
                    val intent =
                        StoreDetailActivity.getIntent(requireContext(), item.storeId, false)
                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
                } else {
                    showToast(R.string.exist_store_error)
                }
            }
        }, object : OnItemClickListener<BossNearStoreResponse.BossNearStoreModel?> {
            override fun onClick(item: BossNearStoreResponse.BossNearStoreModel?) {
                if (item != null) {
                    EventTracker.logEvent(Constants.STORE_CARD_BTN_CLICKED)
                    val intent =
                        FoodTruckStoreDetailActivity.getIntent(requireContext(), item.bossStoreId)
                    startActivity(intent)
                } else {
                    showToast(R.string.exist_store_error)
                }
            }
        },
            object : OnItemClickListener<Popups> {
                override fun onClick(item: Popups) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)))
                }

            }) { item ->
            if (item != null) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.storeId, true)
                startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
            }
        }
        bossCategoriesAdapter = BossCategoriesRecyclerAdapter {
            selectFoodTruck = it.categoryId.toString()
            getNearStore()
        }
        roadFoodCategoriesAdapter = RoadFoodCategoriesRecyclerAdapter {
            selectRoadFood = it.category
            getNearStore()
        }

        viewModel.nearStoreInfo.observe(viewLifecycleOwner) { res ->
            adapter.isAd = res?.find { it is Popups } != null
            adapter.submitList(res)
            val list =
                if (isRoadFoodMode) res?.filterIsInstance<StoreInfo>() else res?.filterIsInstance<BossNearStoreResponse.BossNearStoreModel>()
            naverMapFragment.addStoreMarkers(R.drawable.ic_store_off, list ?: listOf()) {
                onStoreClicked(it)
            }
        }


        binding.rvStore.adapter = adapter
        binding.bossCategoryRecyclerView.adapter = bossCategoriesAdapter
        binding.roadFoodCategoryRecyclerView.adapter = roadFoodCategoriesAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvStore)
        binding.rvStore.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (adapter.getItemLocation(position) != null) {
                            naverMapFragment.updateMarkerIcon(
                                R.drawable.ic_store_off,
                                adapter.focusedIndex
                            )
                            if (adapter.isAd) {
                                adapter.focusedIndex = if (position > 0) position - 1 else position
                            } else {
                                adapter.focusedIndex = position
                            }
                            if (isRoadFoodMode) {
                                naverMapFragment.updateMarkerIcon(
                                    R.drawable.ic_marker,
                                    adapter.focusedIndex
                                )
                            } else {
                                naverMapFragment.updateMarkerIcon(
                                    if (adapter.isFoodTruckOpen(position)) R.drawable.ic_food_truck_clicked_off else R.drawable.ic_marker_green,
                                    adapter.focusedIndex
                                )

                            }

                            adapter.notifyDataSetChanged()
                            adapter.getItemLocation(position)
                                ?.let {
                                    naverMapFragment.moveCameraWithAnim(it)
                                }
                        }
                    }
                })
        )
        binding.tvRetrySearch.setOnClickListener {
            getNearStore()
            binding.tvRetrySearch.isVisible = false
        }
        naverMapFragment.moveToCurrentLocation(false)

        lifecycleScope.launch {
            viewModel.bossCategoryModelList.collect {
                bossCategoriesAdapter.submitList(it)
            }
        }
        lifecycleScope.launch {
            viewModel.roadFoodCategoryModelList.collect {
                roadFoodCategoriesAdapter.submitList(it)
            }
        }
    }

    private fun getNearStore() {
        if (isRoadFoodMode) {
            viewModel.requestHomeItem(naverMapFragment.getMapCenterLatLng(), selectRoadFood)
        } else {
            viewModel.getBossNearStore(naverMapFragment.getMapCenterLatLng(), selectFoodTruck)
        }
    }

    private fun onStoreClicked(adAndStoreItem: AdAndStoreItem) {
        val position = adapter.getItemPosition(adAndStoreItem)
        if (position >= 0) {
            naverMapFragment.updateMarkerIcon(R.drawable.ic_store_off, adapter.focusedIndex)
            adapter.focusedIndex = position
            naverMapFragment.updateMarkerIcon(
                if (isRoadFoodMode) R.drawable.ic_marker else R.drawable.ic_marker_green,
                adapter.focusedIndex
            )
            naverMapFragment.moveCameraWithAnim(
                if (adAndStoreItem is StoreInfo) {
                    LatLng(adAndStoreItem.latitude, adAndStoreItem.longitude)
                } else {
                    val location =
                        (adAndStoreItem as BossNearStoreResponse.BossNearStoreModel).location
                    LatLng(location.latitude, location.longitude)
                }
            )

            adapter.notifyDataSetChanged()
            binding.rvStore.scrollToPosition(position)
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
        naverMapFragment.getMapCenterLatLng().let {
            if (isRoadFoodMode) {
                viewModel.requestHomeItem(it, selectRoadFood)
            } else {
                viewModel.getBossNearStore(it, selectFoodTruck)
            }
        }
    }
}