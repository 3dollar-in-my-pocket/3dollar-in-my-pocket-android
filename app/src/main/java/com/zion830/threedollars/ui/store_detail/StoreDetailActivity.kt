package com.zion830.threedollars.ui.store_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreInfoBinding
import com.zion830.threedollars.repository.model.response.Review
import com.zion830.threedollars.ui.addstore.adapter.MenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.ReviewRecyclerAdapter
import com.zion830.threedollars.ui.report_store.AddReviewDialog
import com.zion830.threedollars.ui.report_store.EditStoreActivity
import com.zion830.threedollars.ui.report_store.StorePhotoDialog
import com.zion830.threedollars.ui.store_detail.map.StoreDetailNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import zion830.com.common.base.BaseActivity
import zion830.com.common.listener.OnItemClickListener

class StoreDetailActivity : BaseActivity<ActivityStoreInfoBinding, StoreDetailViewModel>(R.layout.activity_store_info), OnMapTouchListener {

    override val viewModel: StoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val menuAdapter = MenuRecyclerAdapter()

    private lateinit var reviewAdapter: ReviewRecyclerAdapter

    private var currentPosition: LatLng = NaverMapUtils.DEFAULT_LOCATION

    private var storeId = 0

    override fun onTouch() {
        binding.scroll.requestDisallowInterceptTouchEvent(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        storeId = intent.getIntExtra(KEY_STORE_ID, 0)
        reviewAdapter = ReviewRecyclerAdapter(object : OnItemClickListener<Review> {
            override fun onClick(item: Review) {
                // do nothing
            }
        })

        val naverMapFragment = StoreDetailNaverMapFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.ivStore.setOnClickListener {
            if (viewModel.storeInfo.value?.image?.isNotEmpty() == true) {
                StorePhotoDialog().show(supportFragmentManager, StorePhotoDialog::class.java.name)
            }
        }
        binding.rvMenu.adapter = menuAdapter
        binding.rvReview.adapter = reviewAdapter
        binding.btnAddReview.setOnClickListener {
            AddReviewDialog.getInstance().show(supportFragmentManager, AddReviewDialog::class.java.name)
        }
        binding.btnAddStoreInfo.setOnClickListener {
            startActivityForResult(EditStoreActivity.getIntent(this, storeId), EDIT_STORE_INFO)
        }
        viewModel.addReviewResult.observe(this) {
            viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
        }

        viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
    }

    private fun initMap() {
        val storeId = intent.getIntExtra(KEY_STORE_ID, 0)

        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        viewModel.requestStoreInfo(storeId, it.latitude, it.longitude)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_STORE_INFO && resultCode == Activity.RESULT_OK) {
            viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
            initMap()
        }
    }

    companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"
        private const val EDIT_STORE_INFO = 234

        fun getIntent(context: Context, storeId: Int) = Intent(context, StoreDetailActivity::class.java).apply {
            putExtra(KEY_STORE_ID, storeId)
        }
    }
}