package com.zion830.threedollars.ui.store_detail

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.ads.AdRequest
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreByMenuBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.StoreList
import com.zion830.threedollars.ui.store_detail.adapter.SearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.adapter.SearchByRatingRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.map.StoreByMenuNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreByMenuViewModel
import com.zion830.threedollars.utils.*
import zion830.com.common.base.BaseActivity
import zion830.com.common.listener.OnItemClickListener

class StoreByMenuActivity : BaseActivity<ActivityStoreByMenuBinding, StoreByMenuViewModel>(R.layout.activity_store_by_menu) {

    override val viewModel: StoreByMenuViewModel by viewModels()

    private var currentPosition: LatLng = NaverMapUtils.DEFAULT_LOCATION

    private var menuType: MenuType = MenuType.BUNGEOPPANG

    private val storeByDistanceAdapters = arrayListOf<SearchByDistanceRecyclerAdapter>()

    private val storeByRatingAdapters = arrayListOf<SearchByRatingRecyclerAdapter>()

    override fun initView() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        initAdapter()

        val naverMapFragment = StoreByMenuNaverMapFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        menuType = MenuType.of(intent.getStringExtra(KEY_MENU))
        viewModel.changeCategory(menuType)

        binding.btnMenu1.setOnClickListener {
            viewModel.changeCategory(MenuType.BUNGEOPPANG, currentPosition)
        }
        binding.btnMenu2.setOnClickListener {
            viewModel.changeCategory(MenuType.TAKOYAKI, currentPosition)
        }
        binding.btnMenu3.setOnClickListener {
            viewModel.changeCategory(MenuType.GYERANPPANG, currentPosition)
        }
        binding.btnMenu4.setOnClickListener {
            viewModel.changeCategory(MenuType.HOTTEOK, currentPosition)
        }
        binding.btnSortByDistance.setOnClickListener {
            viewModel.changeSortType(SortType.DISTANCE, currentPosition)
        }
        binding.btnSortByScore.setOnClickListener {
            viewModel.changeSortType(SortType.RATING, currentPosition)
        }
        viewModel.storeByRating.observe(this) {
            storeByRatingAdapters[0].submitList(it.getStoresOver3())
            storeByRatingAdapters[1].submitList(it.storeList2)
            storeByRatingAdapters[2].submitList(it.storeList1)
            storeByRatingAdapters[3].submitList(it.storeList0)
        }
        viewModel.storeByDistance.observe(this) {
            storeByDistanceAdapters[0].submitList(it.storeList50)
            storeByDistanceAdapters[1].submitList(it.storeList100)
            storeByDistanceAdapters[2].submitList(it.storeList500)
            storeByDistanceAdapters[3].submitList(it.storeList1000)
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        val searchByDistanceListener = object : OnItemClickListener<StoreList> {
            override fun onClick(item: StoreList) {
                val intent = StoreDetailActivity.getIntent(this@StoreByMenuActivity, item.id)
                startActivity(intent)
            }
        }
        val searchByRatingListener = object : OnItemClickListener<StoreList> {
            override fun onClick(item: StoreList) {
                val intent = StoreDetailActivity.getIntent(this@StoreByMenuActivity, item.id)
                startActivity(intent)
            }
        }
        storeByDistanceAdapters.apply {
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
        }
        storeByRatingAdapters.apply {
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
        }

        val rvDistances = arrayOf(binding.rvDistance1, binding.rvDistance2, binding.rvDistance3, binding.rvDistance4)
        val rvRatings = arrayOf(binding.rvRating1, binding.rvRating2, binding.rvRating3, binding.rvRating4)
        rvDistances.forEachIndexed { index, recyclerView ->
            recyclerView.adapter = storeByDistanceAdapters[index]
            recyclerView.itemAnimator = null
        }
        rvRatings.forEachIndexed { index, recyclerView ->
            recyclerView.adapter = storeByRatingAdapters[index]
            recyclerView.itemAnimator = null
        }
    }

    companion object {
        private const val KEY_MENU = "KEY_MENU"

        fun getIntent(context: Context, menuType: String) = Intent(context, StoreByMenuActivity::class.java).apply {
            putExtra(KEY_MENU, menuType)
        }
    }
}