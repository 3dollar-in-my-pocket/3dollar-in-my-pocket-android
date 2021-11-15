package com.zion830.threedollars.ui.category

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.ads.AdRequest
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreByMenuBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.repository.model.v2.response.store.StoreList
import com.zion830.threedollars.ui.category.adapter.SearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.category.adapter.SearchByRatingRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.ui.store_detail.map.StoreByMenuNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreByMenuViewModel
import com.zion830.threedollars.utils.*
import kotlinx.android.synthetic.main.activity_store_by_menu.*
import zion830.com.common.base.BaseActivity
import zion830.com.common.listener.OnItemClickListener

class StoreByMenuActivity :
    BaseActivity<ActivityStoreByMenuBinding, StoreByMenuViewModel>(R.layout.activity_store_by_menu) {

    override val viewModel: StoreByMenuViewModel by viewModels()

    private lateinit var menuType: CategoryInfo

    private val listener = object : OnItemClickListener<StoreInfo>{
        override fun onClick(item: StoreInfo) {
            val intent = StoreDetailActivity.getIntent(this@StoreByMenuActivity, item.storeId)
            startActivity(intent)
        }
    }
    private val storeByDistanceAdapters by lazy {
        SearchByDistanceRecyclerAdapter(listener)
    }

    private val storeByRatingAdapters by lazy {
        SearchByRatingRecyclerAdapter(listener)
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
            naverMapFragment.moveToCurrentLocation()
            naverMapFragment.currentPosition?.let { currentPosition ->
                viewModel.changeSortType(SortType.DISTANCE, currentPosition)
            }
        }
        binding.btnSortByScore.setOnClickListener {
            naverMapFragment.moveToCurrentLocation()
            naverMapFragment.currentPosition?.let { currentPosition ->
                viewModel.changeSortType(SortType.RATING, currentPosition)
            }
        }
        viewModel.storeByRating.observe(this) {
            val storeInfoList = it.filter { storeInfo ->
                if (binding.cbCertification.isChecked) {
                    storeInfo.visitHistory.isCertified
                } else {
                    true
                }
            }
                storeByRatingAdapters.submitList(storeInfoList)
        }
        viewModel.storeByDistance.observe(this) {
            val storeInfoList = it.filter { storeInfo ->
                if (binding.cbCertification.isChecked) {
                    storeInfo.visitHistory.isCertified
                } else {
                    true
                }
            }
            storeByDistanceAdapters.submitList(storeInfoList)
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