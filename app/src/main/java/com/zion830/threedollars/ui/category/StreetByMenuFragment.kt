package com.zion830.threedollars.ui.category

import android.content.Intent
import android.net.Uri
import androidx.core.graphics.toColorInt
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdRequest
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentStreetByMenuBinding
import com.zion830.threedollars.datasource.model.v2.AdType
import com.zion830.threedollars.datasource.model.v4.ad.AdResponse
import com.zion830.threedollars.datasource.model.v4.store.StoreResponse
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.category.adapter.StreetSearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.category.adapter.StreetSearchByRatingRecyclerAdapter
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.ui.store_detail.map.StreetStoreByMenuNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StreetStoreByMenuViewModel
import com.zion830.threedollars.utils.OnMapTouchListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.loadUrlImg
import zion830.com.common.listener.OnItemClickListener

@AndroidEntryPoint
class StreetByMenuFragment :
    BaseFragment<FragmentStreetByMenuBinding, StreetStoreByMenuViewModel>(R.layout.fragment_street_by_menu) {

    override val viewModel: StreetStoreByMenuViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private val listener = object : OnItemClickListener<StoreResponse> {
        override fun onClick(item: StoreResponse) {
            EventTracker.logEvent(Constants.STORE_LIST_ITEM_CLICKED)
            val intent = StoreDetailActivity.getIntent(requireContext(), item.storeId)
            startActivity(intent)
        }
    }
    private val adListener = object : OnItemClickListener<AdResponse> {
        override fun onClick(item: AdResponse) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)))
        }
    }
    private val storeByDistanceAdapters by lazy {
        StreetSearchByDistanceRecyclerAdapter(listener, adListener)
    }

    private val storeByRatingAdapters by lazy {
        StreetSearchByRatingRecyclerAdapter(listener, adListener)
    }

    override fun initView() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        initAdapter()

        val naverMapFragment = StreetStoreByMenuNaverMapFragment()
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                // 지도 스크롤 이벤트 구분용
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        parentFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        binding.btnSortByDistance.setOnClickListener {
            EventTracker.logEvent(Constants.ORDER_BY_DISTANCE_BTN_CLICKED)
            naverMapFragment.moveToCurrentLocation()
            naverMapFragment.currentPosition?.let { currentPosition ->
                viewModel.changeSortType(SortType.DISTANCE, currentPosition)
            }
        }
        binding.btnSortByScore.setOnClickListener {
            EventTracker.logEvent(Constants.ORDER_BY_RATING_BTN_CLICKED)
            naverMapFragment.moveToCurrentLocation()
            naverMapFragment.currentPosition?.let { currentPosition ->
                viewModel.changeSortType(SortType.RATING, currentPosition)
            }
        }

        binding.newStoreFloatingButton.setOnClickListener {
            startActivity(
                NewStoreActivity.getInstance(
                    requireContext(),
                    naverMapFragment.getMapCenterLatLng()
                )
            )
        }

        initViewModel(naverMapFragment)
        binding.cbCertification.setOnCheckedChangeListener { _, _ ->
            naverMapFragment.currentPosition?.let {
                viewModel.requestStoreInfo(it)
            }
        }
    }

    private fun initViewModel(naverMapFragment: StreetStoreByMenuNaverMapFragment) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    popupViewModel.adResponse.collect { popups ->
                        if (popups.isNotEmpty()) {
                            binding.itemStoreListAd.run {
                                tvAdTitle.text = popups[0].title
                                popups[0].fontColor?.let {
                                    if (it.isNotEmpty()) {
                                        tvAdTitle.setTextColor(it.toColorInt())
                                        tvAdBody.setTextColor(it.toColorInt())
                                    }
                                }
                                tvAdBody.text = popups[0].subTitle

                                popups[0].bgColor?.let {
                                    if (it.isNotEmpty()) {
                                        layoutItem.setBackgroundColor(it.toColorInt())
                                    }
                                }

                                ivAdImage.loadUrlImg(popups[0].imageUrl)

                                tvDetail.setOnClickListener {
                                    EventTracker.logEvent(Constants.STORE_LIST_AD_BANNER_CLICKED)
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(popups[0].linkUrl)))
                                }
                            }
                            storeByDistanceAdapters.submitAdList(popups)
                            storeByRatingAdapters.submitAdList(popups)
                        }
                    }
                }
            }
        }

        viewModel.category.observe(viewLifecycleOwner) {
            naverMapFragment.currentPosition?.let { currentPosition ->
                viewModel.requestStoreInfo(currentPosition)
            }
        }
        viewModel.storeByRating.observe(viewLifecycleOwner) {
            val storeInfoList = if (binding.cbCertification.isChecked) {
                it.filter { storeInfo -> storeInfo.visitHistory.isCertified }
            } else {
                it
            }
            storeByRatingAdapters.submitList(storeInfoList)
            popupViewModel.getPopups(AdType.STORE_CATEGORY_LIST, null)
        }
        viewModel.storeByDistance.observe(viewLifecycleOwner) {
            val storeInfoList = if (binding.cbCertification.isChecked) {
                it.filter { storeInfo -> storeInfo.visitHistory.isCertified }
            } else {
                it
            }
            storeByDistanceAdapters.submitList(storeInfoList)
            popupViewModel.getPopups(AdType.STORE_CATEGORY_LIST, null)
        }
    }

    private fun initAdapter() {
        binding.run {
            rvDistance.adapter = storeByDistanceAdapters
            rvDistance.itemAnimator = null
            rvRating.adapter = storeByRatingAdapters
            rvRating.itemAnimator = null
        }
    }
}