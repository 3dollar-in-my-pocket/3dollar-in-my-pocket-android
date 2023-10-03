package com.zion830.threedollars.ui.map

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.home.domain.data.store.StatusType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFullScreenMapBinding
import com.zion830.threedollars.ui.DirectionBottomDialog
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailViewModel
import com.zion830.threedollars.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FullScreenMapActivity :
    BaseActivity<ActivityFullScreenMapBinding, FoodTruckStoreDetailViewModel>({ ActivityFullScreenMapBinding.inflate(it) }) {

    override val viewModel: FoodTruckStoreDetailViewModel by viewModels()

    private val naverMapFragment: FullScreenNaverMapFragment by lazy {
        FullScreenNaverMapFragment()
    }
    private var storeId = ""
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun initView() {
        initViewModels()
        initMap()
        initButton()
        initFlows()
    }

    private fun initViewModels() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        storeId = intent.getStringExtra(STORE_ID).toString()
        if (isLocationAvailable() && isGpsAvailable()) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener {
                if (it != null) {
                    viewModel.getFoodTruckStoreDetail(
                        bossStoreId = storeId,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
            }
        }
    }

    private fun initMap() {
        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.directionsTextView.setOnClickListener {
            showDirectionBottomDialog()
        }
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.bossStoreDetailModel.collect { bossStoreDetailModel ->

                        val isClosed = bossStoreDetailModel.openStatusModel.status == StatusType.CLOSED

                        Log.e("asdasd",bossStoreDetailModel.store.location!!.latitude.toString())
                        Log.e("asdasd",bossStoreDetailModel.store.location!!.longitude.toString())
                        if (bossStoreDetailModel.store.location != null) {
                            val latLng = LatLng(bossStoreDetailModel.store.location!!.latitude, bossStoreDetailModel.store.location!!.longitude)
                            naverMapFragment.initMap(latLng, isClosed)
                        }
                    }
                }
            }
        }
    }

    private fun showDirectionBottomDialog() {
        val store = viewModel.bossStoreDetailModel.value.store
        DirectionBottomDialog.getInstance(store.location?.latitude, store.location?.longitude, store.name).show(supportFragmentManager, "")
    }

    companion object {
        const val STORE_ID = "storeId"

        fun getIntent(context: Context, storeId: String? = null) = Intent(context, FullScreenMapActivity::class.java).apply {
            storeId?.let {
                putExtra(STORE_ID, it)
            }
        }
    }
}