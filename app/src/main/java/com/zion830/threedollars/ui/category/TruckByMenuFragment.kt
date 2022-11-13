package com.zion830.threedollars.ui.category

import android.content.Intent
import android.net.Uri
import androidx.core.graphics.toColorInt
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentTruckByMenuBinding
import com.zion830.threedollars.repository.model.v2.response.HomeStoreEmptyResponse
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.BossCategoriesResponse
import com.zion830.threedollars.repository.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.ui.category.adapter.TruckSearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.category.adapter.TruckSearchByReviewRecyclerAdapter
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.store_detail.map.TruckStoreByMenuNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.TruckStoreByMenuViewModel
import com.zion830.threedollars.utils.OnMapTouchListener
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.loadUrlImg
import zion830.com.common.listener.OnItemClickListener

class TruckByMenuFragment :
    BaseFragment<FragmentTruckByMenuBinding, TruckStoreByMenuViewModel>(R.layout.fragment_truck_by_menu) {

    override val viewModel: TruckStoreByMenuViewModel by activityViewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private var menuType: BossCategoriesResponse.BossCategoriesModel =
        BossCategoriesResponse.BossCategoriesModel()

    private val listener = object : OnItemClickListener<BossNearStoreResponse.BossNearStoreModel> {
        override fun onClick(item: BossNearStoreResponse.BossNearStoreModel) {
            EventTracker.logEvent(Constants.STORE_LIST_ITEM_CLICKED)
            val intent = FoodTruckStoreDetailActivity.getIntent(requireContext(), item.bossStoreId)
            startActivity(intent)
        }
    }
    private val adListener = object : OnItemClickListener<Popups> {
        override fun onClick(item: Popups) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)))
        }
    }
    private val truckStoreByDistanceAdapters by lazy {
        TruckSearchByDistanceRecyclerAdapter(listener, adListener)
    }

    private val truckStoreByReviewAdapters by lazy {
        TruckSearchByReviewRecyclerAdapter(listener, adListener)
    }

    override fun initView() {
        initAdapter()

        val naverMapFragment = TruckStoreByMenuNaverMapFragment()
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                // 지도 스크롤 이벤트 구분용
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        parentFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        binding.categoryImageView.setOnClickListener {
            val bottomSheetDialog = TruckSelectCategoryDialogFragment()
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
                viewModel.changeSortType(SortType.REVIEW, currentPosition)
            }
        }

        popupViewModel.popups.observe(viewLifecycleOwner) { popups ->
            popups?.let {
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
                    truckStoreByDistanceAdapters.submitAdList(popups)
                    truckStoreByReviewAdapters.submitAdList(popups)
                }
            }
        }
        viewModel.category.observe(viewLifecycleOwner) {
            naverMapFragment.currentPosition?.let { currentPosition ->
                viewModel.requestStoreInfo(currentPosition)
            }
        }
        viewModel.storeByReview.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    truckStoreByReviewAdapters.submitEmptyList(
                        listOf(
                            HomeStoreEmptyResponse(
                                emptyTitle = R.string.recruit_boss_title,
                                emptyBody = R.string.recruit_boss_body
                            )
                        )
                    )
                } else {
                    truckStoreByReviewAdapters.submitList(it)
                }
                popupViewModel.getPopups("STORE_CATEGORY_LIST")
            }
        }
        viewModel.storeByDistance.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isEmpty()) {
                    truckStoreByDistanceAdapters.submitEmptyList(
                        listOf(
                            HomeStoreEmptyResponse(
                                emptyTitle = R.string.recruit_boss_title,
                                emptyBody = R.string.recruit_boss_body
                            )
                        )
                    )
                } else {
                    truckStoreByDistanceAdapters.submitList(it)
                }
                popupViewModel.getPopups("STORE_CATEGORY_LIST")
            }
        }
    }

    private fun initAdapter() {
        binding.run {
            rvDistance.adapter = truckStoreByDistanceAdapters
            rvDistance.itemAnimator = null
            rvRating.adapter = truckStoreByReviewAdapters
            rvRating.itemAnimator = null
        }
    }
}