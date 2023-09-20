package com.zion830.threedollars.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.firebase.messaging.FirebaseMessaging
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.ContentModel
import com.home.presentation.data.HomeSortType
import com.home.presentation.data.HomeStoreType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.listener.OnSnapPositionChangeListener
import com.threedollar.common.listener.SnapOnScrollListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.ui.MarketingDialog
import com.zion830.threedollars.ui.addstore.view.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreRecyclerAdapter
import com.zion830.threedollars.utils.getCurrentLocationName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: AroundStoreRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    private var selectRoadFood = "All"

    private var homeStoreType = HomeStoreType.ALL
    private var homeSortType = HomeSortType.DISTANCE_ASC

    override fun initView() {
        viewModel.getUserInfo()
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
        getNearStore()

        binding.layoutAddress.setOnClickListener {
            EventTracker.logEvent(Constants.SEARCH_BTN_CLICKED)
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                SearchAddressFragment.newInstance(),
                SearchAddressFragment::class.java.name
            )
        }

        binding.allMenuTextView.setOnClickListener {
            showSelectCategoryDialog()
        }

        binding.filterTextView.setOnClickListener {
            homeSortType = if (homeSortType == HomeSortType.DISTANCE_ASC) {
                HomeSortType.LATEST
            } else {
                HomeSortType.DISTANCE_ASC
            }
            viewModel.updateHomeFilterEvent(homeSortType = homeSortType)
        }

        binding.bossFilterTextView.setOnClickListener {
            homeStoreType = if (homeStoreType == HomeStoreType.ALL) HomeStoreType.BOSS_STORE else HomeStoreType.ALL
            viewModel.updateHomeFilterEvent(homeStoreType = homeStoreType)
        }

        binding.listViewTextView.setOnClickListener {
            // TODO: 리스트뷰 기능 구현
        }

        adapter = AroundStoreRecyclerAdapter(object : OnItemClickListener<ContentModel> {
            override fun onClick(item: ContentModel) {
//                if (item != null) {
//                    EventTracker.logEvent(Constants.STORE_CARD_BTN_CLICKED)
//                    val intent =
//                        StoreDetailActivity.getIntent(requireContext(), item.storeId, false)
//                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
//                } else {
//                    showToast(R.string.exist_store_error)
//                }
            }
        }, object : OnItemClickListener<AdvertisementModel> {
            override fun onClick(item: AdvertisementModel) {
                EventTracker.logEvent(Constants.HOME_AD_BANNER_CLICKED)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)))
            }

        }) { item ->
//            if (item != null) {
//                val intent = StoreDetailActivity.getIntent(requireContext(), item.storeId, true)
//                startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
//            }
        }

        binding.aroundStoreRecyclerView.adapter = adapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.aroundStoreRecyclerView)
        binding.aroundStoreRecyclerView.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (adapter.getItemLocation(position) != null) {
                            naverMapFragment.updateMarkerIcon(R.drawable.ic_store_off, adapter.focusedIndex)
                            adapter.focusedIndex = position
                            naverMapFragment.updateMarkerIcon(R.drawable.ic_marker, adapter.focusedIndex)

                            adapter.getItemLocation(position)?.let {
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

        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.selectCategory.collect {
                        getNearStore()
                    }
                }
                launch {
                    viewModel.userInfo.collect {
                        if (it.marketingConsent == "UNVERIFIED") {
                            showMarketingDialog()
                        }
                    }
                }

                launch {
                    viewModel.aroundStoreModels.collect { adAndStoreItems ->
                        if (adAndStoreItems.isEmpty()) {
                            adapter.submitList(listOf(StoreEmptyResponse()))
                        } else {
                            adapter.submitList(adAndStoreItems)
                        }
                        val list = adAndStoreItems.filterIsInstance<ContentModel>()
                        naverMapFragment.addStoreMarkers(R.drawable.ic_store_off, list) {
                            onStoreClicked(it)
                        }
                    }
                }
                launch {
                    viewModel.homeFilterEvent.collect {
                        getNearStore()

                        val textColor = resources.getColor(if (it.homeStoreType == HomeStoreType.BOSS_STORE) R.color.gray70 else R.color.gray40, null)
                        val drawableStart = ContextCompat.getDrawable(
                            GlobalApplication.getContext(),
                            if (it.homeStoreType == HomeStoreType.BOSS_STORE) R.drawable.ic_check_gray_16 else R.drawable.ic_uncheck
                        )
                        binding.run {
                            bossFilterTextView.setTextColor(textColor)
                            bossFilterTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
                            filterTextView.text = if (it.homeSortType == HomeSortType.DISTANCE_ASC) {
                                getString(R.string.fragment_home_filter_latest)
                            } else {
                                getString(R.string.fragment_home_filter_distance)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showSelectCategoryDialog() {
        val dialog = SelectCategoryDialogFragment()
        dialog.show(parentFragmentManager, "")
    }

    private fun getNearStore() {
        viewModel.requestHomeItem(
            naverMapFragment.getMapCenterLatLng()
        )
    }

    private fun onStoreClicked(adAndStoreItem: AdAndStoreItem) {
        val position = adapter.getItemPosition(adAndStoreItem)
        if (position >= 0) {
            naverMapFragment.updateMarkerIcon(R.drawable.ic_store_off, adapter.focusedIndex)
            adapter.focusedIndex = position
            naverMapFragment.updateMarkerIcon(R.drawable.ic_marker, adapter.focusedIndex)
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
                        viewModel.postPushInformation(pushToken = it.result, isMarketing = isMarketing)
                    }
                }
            }
        })
        dialog.show(parentFragmentManager, dialog.tag)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GET_LOCATION_PERMISSION) {
            naverMapFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        getNearStore()
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)
}