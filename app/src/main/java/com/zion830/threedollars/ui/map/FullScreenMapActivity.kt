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
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFullScreenMapBinding
import com.zion830.threedollars.ui.DirectionBottomDialog
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailViewModel
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FullScreenMapActivity :
    BaseActivity<ActivityFullScreenMapBinding, BaseViewModel>({ ActivityFullScreenMapBinding.inflate(it) }) {

    override val viewModel: BaseViewModel by viewModels()

    private val naverMapFragment: FullScreenNaverMapFragment by lazy {
        FullScreenNaverMapFragment()
    }
    private var latitude = 0.0
    private var longitude = 0.0
    private var name = ""
    private var isClosed = false

    override fun initView() {
        initIntent()
        initMap()
        initButton()
    }

    private fun initIntent() {
        latitude = intent.getDoubleExtra(LATITUDE, 0.0)
        longitude = intent.getDoubleExtra(LONGITUDE, 0.0)
        name = intent.getStringExtra(NAME).toString()
        isClosed = intent.getBooleanExtra(IS_CLOSED, true)
    }

    private fun initMap() {
        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()
        val latLng = LatLng(latitude, longitude)
        naverMapFragment.initMap(latLng, isClosed)
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.directionsTextView.setOnClickListener {
            showDirectionBottomDialog()
        }
    }

    private fun showDirectionBottomDialog() {
        DirectionBottomDialog.getInstance(latitude, longitude, name).show(supportFragmentManager, "")
    }

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val NAME = "name"
        const val IS_CLOSED = "isClosed"

        fun getIntent(context: Context, latitude: Double?, longitude: Double?, name: String?, isClosed: Boolean? = true) =
            Intent(context, FullScreenMapActivity::class.java).apply {
                latitude?.let {
                    putExtra(LATITUDE, it)
                }
                longitude?.let {
                    putExtra(LONGITUDE, it)
                }
                name?.let {
                    putExtra(NAME, it)
                }
                isClosed?.let {
                    putExtra(IS_CLOSED, it)
                }
            }
    }
}