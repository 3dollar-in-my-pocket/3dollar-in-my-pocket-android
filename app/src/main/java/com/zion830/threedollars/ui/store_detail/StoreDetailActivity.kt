package com.zion830.threedollars.ui.store_detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreInfoBinding
import com.zion830.threedollars.ui.addstore.adapter.MenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.ReviewRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.*
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.addNewFragment

class StoreDetailActivity : BaseActivity<ActivityStoreInfoBinding, StoreDetailViewModel>(R.layout.activity_store_info) {

    override val viewModel: StoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mapFragment: SupportMapFragment

    private val menuAdapter = MenuRecyclerAdapter()

    private val reviewAdapter = ReviewRecyclerAdapter()

    private var googleMap: GoogleMap? = null

    private var currentPosition: LatLng = DEFAULT_LOCATION

    private var storeId = 0

    override fun initView() {
        storeId = intent.getIntExtra(KEY_STORE_ID, 0)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            initMap()
            binding.btnFindLocation.setOnClickListener {
                moveToCurrentPosition()
            }
            viewModel.storeLocation.observe(this) {
                map.setMarker(it)
                moveCameraTo(it, DEFAULT_ZOOM)
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.ivStore.setOnClickListener {
            if (viewModel.storeInfo.value?.image?.isNotEmpty() == true) {
                supportFragmentManager.addNewFragment(R.id.container, StorePhotoFragment(), StorePhotoFragment::class.java.name)
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
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false

        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        moveCameraTo(it.toLatLng(), DEFAULT_ZOOM)
                        viewModel.requestStoreInfo(storeId, it.latitude, it.longitude)
                    }
                }
                googleMap?.isMyLocationEnabled = true
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    private fun moveToCurrentPosition() {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    currentPosition = it.toLatLng()
                    moveCameraTo(it.toLatLng(), googleMap?.cameraPosition?.zoom)
                }
            } else {
                showToast(R.string.find_location_error)
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

    private fun moveCameraTo(position: LatLng?, zoomLevel: Float?) {
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel ?: DEFAULT_ZOOM))
    }

    companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"
        private const val EDIT_STORE_INFO = 234

        fun getIntent(context: Context, storeId: Int) = Intent(context, StoreDetailActivity::class.java).apply {
            putExtra(KEY_STORE_ID, storeId)
        }
    }
}