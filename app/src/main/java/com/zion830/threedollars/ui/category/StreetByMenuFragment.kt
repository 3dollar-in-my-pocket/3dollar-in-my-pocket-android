package com.zion830.threedollars.ui.category

import android.content.Intent
import android.net.Uri
import androidx.core.graphics.toColorInt
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdRequest
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentStreetByMenuBinding
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.category.adapter.StreetSearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.category.adapter.StreetSearchByRatingRecyclerAdapter
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.ui.store_detail.map.StreetStoreByMenuNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StreetStoreByMenuViewModel
import com.zion830.threedollars.utils.OnMapTouchListener
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.loadUrlImg
import zion830.com.common.listener.OnItemClickListener

class StreetByMenuFragment :
    BaseFragment<FragmentStreetByMenuBinding, StreetStoreByMenuViewModel>(R.layout.fragment_street_by_menu),
    OnMapTouchListener {

    override val viewModel: StreetStoreByMenuViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private var menuType: CategoryInfo = CategoryInfo()

    private val listener = object : OnItemClickListener<StoreInfo> {
        override fun onClick(item: StoreInfo) {
            EventTracker.logEvent(Constants.STORE_LIST_ITEM_CLICKED)
            val intent = StoreDetailActivity.getIntent(requireContext(), item.storeId)
            startActivity(intent)
        }
    }
    private val adListener = object : OnItemClickListener<Popups> {
        override fun onClick(item: Popups) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)))
        }
    }
    private val storeByDistanceAdapters by lazy {
        StreetSearchByDistanceRecyclerAdapter(listener, adListener)
    }

    private val storeByRatingAdapters by lazy {
        StreetSearchByRatingRecyclerAdapter(listener, adListener)
    }

    override fun onTouch() {
        // 지도 스크롤 이벤트 구분용
        binding.scroll.requestDisallowInterceptTouchEvent(true)
    }

    override fun initView() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        initAdapter()

        val naverMapFragment = StreetStoreByMenuNaverMapFragment(this)
        parentFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        binding.categoryImageView.setOnClickListener {
            val bottomSheetDialog = StreetSelectCategoryDialogFragment()
            bottomSheetDialog.show(parentFragmentManager, "")
        }
        binding.btnMenu.setOnClickListener {
            naverMapFragment.moveToCurrentLocation()
            naverMapFragment.currentPosition?.let { currentPosition ->
                viewModel.changeCategory(menuType, currentPosition)
            }
        }
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

        popupViewModel.popups.observe(viewLifecycleOwner) { popups ->
            if (popups.isNotEmpty()) {
                binding.itemStoreListAd.run {
                    tvAdTitle.text = popups[0].title
                    popups[0].fontColor?.let {
                        tvAdTitle.setTextColor(it.toColorInt())
                        tvAdBody.setTextColor(it.toColorInt())
                    }
                    tvAdBody.text = popups[0].subTitle

                    popups[0].bgColor?.let { layoutItem.setBackgroundColor(it.toColorInt()) }

                    ivAdImage.loadUrlImg(popups[0].imageUrl)

                    tvDetail.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(popups[0].linkUrl)))
                    }
                }
                storeByDistanceAdapters.submitAdList(popups)
                storeByRatingAdapters.submitAdList(popups)
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
            popupViewModel.getPopups("STORE_CATEGORY_LIST")
        }
        viewModel.storeByDistance.observe(viewLifecycleOwner) {
            val storeInfoList = if (binding.cbCertification.isChecked) {
                it.filter { storeInfo -> storeInfo.visitHistory.isCertified }
            } else {
                it
            }
            storeByDistanceAdapters.submitList(storeInfoList)
            popupViewModel.getPopups("STORE_CATEGORY_LIST")
        }
        binding.cbCertification.setOnCheckedChangeListener { _, _ ->
            naverMapFragment.currentPosition?.let {
                viewModel.requestStoreInfo(it)
            }
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