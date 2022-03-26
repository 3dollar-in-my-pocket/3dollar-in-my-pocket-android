package com.zion830.threedollars.ui.category

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import com.google.android.gms.ads.AdRequest
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreByMenuBinding
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.category.adapter.SearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.category.adapter.SearchByRatingRecyclerAdapter
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.ui.store_detail.map.StoreByMenuNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreByMenuViewModel
import com.zion830.threedollars.utils.*
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.loadUrlImg
import zion830.com.common.listener.OnItemClickListener

class StoreByMenuActivity :
    BaseActivity<ActivityStoreByMenuBinding, StoreByMenuViewModel>(R.layout.activity_store_by_menu),
    OnMapTouchListener {

    override val viewModel: StoreByMenuViewModel by viewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private lateinit var menuType: CategoryInfo

    private val listener = object : OnItemClickListener<StoreInfo> {
        override fun onClick(item: StoreInfo) {
            EventTracker.logEvent(Constants.STORE_LIST_ITEM_CLICKED)
            val intent = StoreDetailActivity.getIntent(this@StoreByMenuActivity, item.storeId)
            startActivity(intent)
        }
    }
    private val adListener = object : OnItemClickListener<Popups> {
        override fun onClick(item: Popups) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)))
        }
    }
    private val storeByDistanceAdapters by lazy {
        SearchByDistanceRecyclerAdapter(listener, adListener)
    }

    private val storeByRatingAdapters by lazy {
        SearchByRatingRecyclerAdapter(listener, adListener)
    }

    override fun onTouch() {
        // 지도 스크롤 이벤트 구분용
        binding.scroll.requestDisallowInterceptTouchEvent(true)
    }

    override fun initView() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        initAdapter()

        val naverMapFragment = StoreByMenuNaverMapFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        menuType = intent.getSerializableExtra(KEY_MENU) as? CategoryInfo ?: CategoryInfo()
        viewModel.changeCategory(menuType)

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
        popupViewModel.popups.observe(this, { popups ->
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
        })
        viewModel.storeByRating.observe(this) {
            val storeInfoList = if (binding.cbCertification.isChecked) {
                it.filter { storeInfo -> storeInfo.visitHistory.isCertified }
            } else {
                it
            }
            storeByRatingAdapters.submitList(storeInfoList)
            popupViewModel.getPopups("STORE_CATEGORY_LIST")
        }
        viewModel.storeByDistance.observe(this) {
            val storeInfoList = if (binding.cbCertification.isChecked) {
                it.filter { storeInfo -> storeInfo.visitHistory.isCertified }
            } else {
                it
            }
            storeByDistanceAdapters.submitList(storeInfoList)
            popupViewModel.getPopups("STORE_CATEGORY_LIST")
        }
        binding.btnBack.setOnClickListener {
            finish()
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

    companion object {
        private const val KEY_MENU = "KEY_MENU"

        fun getIntent(context: Context, category: CategoryInfo) =
            Intent(context, StoreByMenuActivity::class.java).apply {
                putExtra(KEY_MENU, category)
            }
    }
}